/**
 * Cliente HTTP simple para centralizar las llamadas al backend de Spring Boot.
 * Se encarga de adjuntar el token JWT, manejar errores y exponer helpers de autenticacion.
 */
(() => {
    const API_BASE_URL = window.API_BASE_URL || 'http://localhost:8080';
    const TOKEN_KEY = 'tombers.auth.token';
    const USER_KEY = 'tombers.auth.user';

    /** Obtiene el token almacenado en sessionStorage. */
    const obtenerToken = () => sessionStorage.getItem(TOKEN_KEY);

    /** Obtiene el usuario autenticado almacenado. */
    const obtenerUsuario = () => {
        const raw = sessionStorage.getItem(USER_KEY);
        return raw ? JSON.parse(raw) : null;
    };

    /** Guarda el token y los datos de usuario devueltos por el backend. */
    const guardarSesion = ({ token, user }) => {
        if (token) {
            sessionStorage.setItem(TOKEN_KEY, token);
        }
        if (user) {
            sessionStorage.setItem(USER_KEY, JSON.stringify(user));
        }
    };

    /** Elimina la informacion de sesion almacenada en el navegador. */
    const limpiarSesion = () => {
        sessionStorage.removeItem(TOKEN_KEY);
        sessionStorage.removeItem(USER_KEY);
    };

    /** Determina si existe un token activo. */
    const estaAutenticado = () => Boolean(obtenerToken());

    /** Construye los encabezados finales para la peticion. */
    const construirHeaders = (headers = {}, skipAuth = false) => {
        const result = {
            Accept: 'application/json',
            ...headers,
        };

        if (!skipAuth) {
            const token = obtenerToken();
            if (token) {
                result.Authorization = `Bearer ${token}`;
            }
        }

        return result;
    };

    /**
     * Realiza una peticion al backend y maneja los casos comunes.
     * Lanza un Error con informacion adicional cuando la respuesta no es exitosa.
     */
    const request = async (path, { method = 'GET', body, headers, skipAuth = false } = {}) => {
        const config = {
            method,
            headers: construirHeaders(headers, skipAuth),
        };

        if (body !== undefined && body !== null) {
            if (body instanceof FormData) {
                config.body = body;
            } else {
                config.body = JSON.stringify(body);
                if (!config.headers['Content-Type']) {
                    config.headers['Content-Type'] = 'application/json';
                }
            }
        }

        const response = await fetch(`${API_BASE_URL}${path}`, config);
        const contentType = response.headers.get('content-type') || '';
        let payload = null;

        if (contentType.includes('application/json')) {
            try {
                payload = await response.json();
            } catch (error) {
                payload = null;
            }
        } else {
            payload = await response.text();
        }

        if (!response.ok) {
            if (response.status === 401) {
                limpiarSesion();
                if (!skipAuth) {
                    window.location.href = '/';
                }
            }

            const message = (payload && (payload.message || payload.error)) || 'Error al comunicarse con el backend';
            const error = new Error(message);
            error.status = response.status;
            error.data = payload;
            throw error;
        }

        return payload;
    };

    /*** Helpers especificos de autenticacion ***/
    const login = async (credentials) => {
        const data = await request('/api/auth/login', {
            method: 'POST',
            body: credentials,
            skipAuth: true,
        });
        guardarSesion(data);
        return data;
    };

    const register = async (payload) => {
        const data = await request('/api/auth/register', {
            method: 'POST',
            body: payload,
            skipAuth: true,
        });
        guardarSesion(data);
        return data;
    };

    const logout = () => {
        limpiarSesion();
    };

    const apiClient = {
        baseUrl: API_BASE_URL,
        request,
        get: (path, options = {}) => request(path, { ...options, method: 'GET' }),
        post: (path, body, options = {}) => request(path, { ...options, method: 'POST', body }),
        put: (path, body, options = {}) => request(path, { ...options, method: 'PUT', body }),
        delete: (path, options = {}) => request(path, { ...options, method: 'DELETE' }),
        auth: {
            login,
            register,
            logout,
            getToken: obtenerToken,
            getUser: obtenerUsuario,
            isAuthenticated: estaAutenticado,
            clearSession: limpiarSesion,
        },
    };

    window.apiClient = apiClient;
})();
