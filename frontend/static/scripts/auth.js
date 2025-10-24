// Archivo: static/scripts/auth.js
// Gestiona el flujo de autenticación del frontend y delega las operaciones al backend Spring Boot.

class AuthManager {
    constructor() {
        this.loginForm = document.getElementById('loginForm');
        this.registerForm = document.getElementById('registerForm');
        this.formsContainer = document.querySelector('.auth-forms');
        this.passwordStrength = document.getElementById('passwordStrength');
        this.notification = document.getElementById('notification');
        this.loadingOverlay = document.getElementById('loadingOverlay');
        this.notificationTimeout = null;
        this.validationSummary = [];// Guarda mensajes de error del ciclo actual.
        this.collectingSummary = false;// Marca cuando debemos agrupar mensajes.
        this.lastValidationSummary = ''; // Resumen armado en la ultima validacion.
        this.init();
    }

    /**
     * Inicializa el módulo validando la sesión actual y registrando los eventos necesarios.
     */
    init() {
        if (!window.apiClient) {
            console.error('El objeto apiClient no está disponible');
            return;
        }

        if (window.apiClient.auth.isAuthenticated()) {
            window.location.href = '/feed';
            return;
        }

        this.registerEventListeners();
        this.registerRealtimeValidation();
        this.bindToggleButtons();
        const initialForm = this.resolveInitialForm();
        if (initialForm === 'register') {
            this.showRegisterForm();
        } else {
            this.showLoginForm();
        }
        this.hideNotification();
    }

    /**
     * Registra los listeners de envío de formularios y cambios básicos.
     */
    registerEventListeners() {
        if (this.loginForm) {
            this.loginForm.addEventListener('submit', (event) => this.handleLogin(event));
        }

        if (this.registerForm) {
            this.registerForm.addEventListener('submit', (event) => this.handleRegister(event));
        }
    }

    /**
     * Asocia los botones que alternan entre login y registro.
     */
    bindToggleButtons() {
        const registerButtons = document.querySelectorAll('[data-auth-toggle="register"]');
        registerButtons.forEach((button) => {
            button.addEventListener('click', (event) => {
                event.preventDefault();
                this.showRegisterForm();
            });
        });

        const loginButtons = document.querySelectorAll('[data-auth-toggle="login"]');
        loginButtons.forEach((button) => {
            button.addEventListener('click', (event) => {
                event.preventDefault();
                this.showLoginForm();
            });
        });
    }

    /**
     * Determina qué formulario se debe mostrar inicialmente.
     */
    resolveInitialForm() {
        const params = new URLSearchParams(window.location.search);
        const viewParam = (params.get('auth') || params.get('view') || '').toLowerCase();
        if (viewParam === 'register') {
            return 'register';
        }

        const hash = (window.location.hash || '').replace('#', '').toLowerCase();
        if (hash === 'register') {
            return 'register';
        }

        return 'login';
    }

    /**
     * Cambia el formulario activo y actualiza los estados visuales.
     */
    setActiveForm(target) {
        const normalized = target === 'register' ? 'register' : 'login';
        const showRegister = normalized === 'register';

        this.clearErrors();

        if (this.loginForm) {
            this.loginForm.classList.toggle('active', !showRegister);
            this.loginForm.setAttribute('aria-hidden', showRegister ? 'true' : 'false');
        }

        if (this.registerForm) {
            this.registerForm.classList.toggle('active', showRegister);
            this.registerForm.setAttribute('aria-hidden', showRegister ? 'false' : 'true');
        }

        if (this.formsContainer) {
            this.formsContainer.classList.toggle('login-active', !showRegister);
            this.formsContainer.classList.toggle('register-active', showRegister);
            this.formsContainer.setAttribute('data-current-form', normalized);
        }

        this.resetPasswordStrength();
    }

    /**
     * Muestra el formulario de acceso.
     */
    showLoginForm() {
        this.setActiveForm('login');
        const firstInput = this.loginForm?.querySelector('input, select, textarea');
        firstInput?.focus();
    }

    /**
     * Muestra el formulario de registro.
     */
    showRegisterForm() {
        this.setActiveForm('register');
        const firstInput = this.registerForm?.querySelector('input, select, textarea');
        firstInput?.focus();
    }

    /**
     * Limpia el indicador de fortaleza de contraseña.
     */
    resetPasswordStrength() {
        if (this.passwordStrength) {
            this.passwordStrength.textContent = '';
        }
    }

    /**
     * Configura validaciones en tiempo real para mejorar la experiencia del usuario.
     */
    registerRealtimeValidation() {
        if (!this.registerForm) {
            return;
        }

        const firstNameInput = document.getElementById('firstName');
        const lastNameInput = document.getElementById('lastName');
        const passwordInput = document.getElementById('registerPassword');
        const confirmPasswordInput = document.getElementById('confirmPassword');
        const emailInput = document.getElementById('registerEmail');
        const usernameInput = document.getElementById('username');

        // Validaciones inmediatas para mostrar errores especificos en cada campo.
        firstNameInput?.addEventListener('blur', () => this.validateNameField('firstName', 'firstNameError', 'El nombre'));
        lastNameInput?.addEventListener('blur', () => this.validateNameField('lastName', 'lastNameError', 'El apellido'));
        passwordInput?.addEventListener('input', () => this.updatePasswordStrength(passwordInput.value));
        confirmPasswordInput?.addEventListener('input', () => this.validatePasswordMatch());
        emailInput?.addEventListener('blur', () => this.validateEmail('registerEmail', 'registerEmailError'));
        usernameInput?.addEventListener('blur', () => this.validateUsername());
    }


    /**
     * Envía las credenciales al backend y gestiona la navegación posterior.
     */
    async handleLogin(event) {
        event.preventDefault();

        this.clearErrors();
        // Limpiamos mensajes anteriores para que los errores actuales se vean claros.

        const emailInput = document.getElementById('loginEmail');
        const passwordInput = document.getElementById('loginPassword');
        const email = emailInput?.value.trim() || '';
        const password = passwordInput?.value || '';

        // Comenzamos a agrupar mensajes claros para el usuario.
        this.collectingSummary = true;

        // Validamos el correo con textos pensados para el login.
        const emailValid = this.validateEmail('loginEmail', 'loginEmailError', {
            required: 'Ingresa tu correo electronico en formato usuario@dominio.com.',
            invalid: 'El correo debe tener el formato usuario@dominio.com (sin espacios).',
        });

        let passwordValid = true;
        if (!password) {
            this.setFieldError('loginPasswordError', 'Ingresa tu contrasena para continuar.');
            passwordValid = false;
        } else {
            this.setFieldError('loginPasswordError', '');
        }

        this.collectingSummary = false;

        if (!emailValid || !passwordValid) {
            const summary = this.buildSummaryMessage('Corrige los errores antes de continuar.');
            this.showNotification(summary, 'error');
            this.validationSummary = [];
            return;
        }

        try {
            this.toggleLoading(true);
            await window.apiClient.auth.login({ email, password });
            this.showNotification('Inicio de sesion exitoso, redirigiendo...', 'success');
            setTimeout(() => {
                window.location.href = '/feed';
            }, 1200);
        } catch (error) {
            let message = error?.data?.detail || error?.message || 'No fue posible iniciar sesion.';
            if (error?.status === 401) {
                message = 'Email o contrasena incorrectos.';
            }
            if (error?.status === 400 && /(validaci(o|\u00f3)n)/i.test(message)) {
                message = 'Revisa el formato de los datos ingresados.';
            }
            this.showNotification(message, 'error');
            this.applyLoginErrors(error, message);
        } finally {
            this.validationSummary = [];
            this.collectingSummary = false;
            this.toggleLoading(false);
        }
    }


    /**
     * Valida los datos del formulario de registro y crea la cuenta en el backend.
     */
    async handleRegister(event) {
        event.preventDefault();

        if (!this.validateRegisterForm()) {
            const summary = this.lastValidationSummary || this.buildSummaryMessage('Revisa los errores marcados antes de continuar.');
            this.showNotification(summary, 'error');
            this.validationSummary = [];
            return;
        }

        const form = new FormData(this.registerForm);
        const skills = this.parseSkills(form.get('skills'));

        const payload = {
            firstName: form.get('firstName')?.trim(),
            lastName: form.get('lastName')?.trim(),
            email: form.get('email')?.trim(),
            username: form.get('username')?.trim(),
            password: form.get('password'),
            skills,
            age: null,
            birthDate: null,
            languages: null,
            specialization: null,
            phone: null,
            linkedin: null,
            github: null,
            portfolio: null,
            bio: null,
            certifications: [],
            interests: [],
        };

        try {
            this.toggleLoading(true);
            const response = await window.apiClient.auth.register(payload);
            const mensaje = response?.message || 'Registro exitoso, configurá tu perfil.';
            this.showNotification(mensaje, 'success');
            setTimeout(() => {
                window.location.href = '/feed';
            }, 1500);
        } catch (error) {
            const message = error?.data?.detail || error?.message || 'No fue posible crear la cuenta.';
            this.showNotification(message, 'error');
            this.applyRegisterErrors(error);
        } finally {
            this.toggleLoading(false);
        }
    }

    /**
     * Marca los campos del registro que provienen de errores del backend.
     */
    applyRegisterErrors(error) {
        const fieldMap = {
            firstName: 'firstNameError',
            lastName: 'lastNameError',
            email: 'registerEmailError',
            username: 'usernameError',
            password: 'registerPasswordError',
            confirmPassword: 'confirmPasswordError',
            acceptTerms: 'acceptTermsError',
            termsAccepted: 'acceptTermsError',
        };

        const backendErrors = error?.data?.errores || error?.data?.errors;
        if (backendErrors && typeof backendErrors === 'object') {
            // Si el backend envio detalles por campo, los mostramos tal cual.
            Object.entries(backendErrors).forEach(([field, detail]) => {
                const targetId = fieldMap[field] || `${field}Error`;
                const message = Array.isArray(detail) ? detail.join(' ') : detail;
                if (typeof message === 'string') {
                    this.setFieldError(targetId, message);
                }
            });
            return;
        }

        const fallbackMessage = error?.message || '';
        if (/email/i.test(fallbackMessage)) {
            this.setFieldError('registerEmailError', fallbackMessage);
        }
        if (/(usuario|username)/i.test(fallbackMessage)) {
            this.setFieldError('usernameError', fallbackMessage);
        }
    }


    /**
     * Gestiona los errores devueltos por el backend en el inicio de sesion.
     */
    applyLoginErrors(error, fallbackMessage) {
        const backendErrors = error?.data?.errors || error?.data?.errores;

        if (backendErrors && typeof backendErrors === 'object') {
            // Si el backend envio detalles por campo, los mostramos tal cual.
            const emailError = backendErrors.email || backendErrors.username;
            const passwordError = backendErrors.password;

            if (emailError) {
                const message = Array.isArray(emailError) ? emailError.join(' ') : emailError;
                this.setFieldError('loginEmailError', message);
            }

            if (passwordError) {
                const message = Array.isArray(passwordError) ? passwordError.join(' ') : passwordError;
                this.setFieldError('loginPasswordError', message);
            }

            return;
        }

        const comparableMessage = fallbackMessage || error?.message || '';
        // Detectamos errores comunes de formato o credenciales para dar contexto util.
        if (error?.status === 400 && /(validaci(o|\u00f3)n)/i.test(comparableMessage)) {
            this.setFieldError('loginEmailError', 'El correo no tiene un formato valido (ejemplo: usuario@dominio.com).');
            return;
        }

        if (error?.status === 401) {
            this.setFieldError('loginEmailError', 'Verifica que el correo sea correcto.');
            this.setFieldError('loginPasswordError', 'La contrasena ingresada no es correcta.');
            return;
        }

        if (fallbackMessage) {
            this.setFieldError('loginPasswordError', fallbackMessage);
        }
    }


    /**
     * Convierte el campo de habilidades en una estructura compatible con el backend.
     */
    parseSkills(rawValue) {
        if (!rawValue) {
            return [];
        }

        return rawValue
            .split(',')
            .map((skill) => skill.trim())
            .filter(Boolean)
            .map((nombre) => ({ nombre, nivel: 'Intermedio' }));
    }

    /**
     * Valida nombres y apellidos para mostrar mensajes claros al usuario.
     */
    validateNameField(fieldId, errorId, label) {
        const input = document.getElementById(fieldId);
        const value = input?.value.trim() || '';
        const regex = /^[A-Za-zÀ-ÖØ-öø-ÿ'\s-]{2,40}$/;

        if (!value) {
            this.setFieldError(errorId, `${label} es obligatorio.`);
            return false;
        }

        if (!regex.test(value)) {
            this.setFieldError(errorId, `${label} solo puede incluir letras, espacios o guiones (2 a 40 caracteres).`);
            return false;
        }

        this.setFieldError(errorId, '');
        return true;
    }


    /**
     * Valida los datos obligatorios del formulario de registro.
     */
    validateRegisterForm() {
        this.clearErrors();

        let valid = true;
        // Validamos cada bloque para devolverle al usuario un mensaje puntual.

        this.collectingSummary = true;

        const nameValid = this.validateNameField('firstName', 'firstNameError', 'El nombre');
        valid = nameValid && valid;

        const lastNameValid = this.validateNameField('lastName', 'lastNameError', 'El apellido');
        valid = lastNameValid && valid;

        const emailValid = this.validateEmail('registerEmail', 'registerEmailError');
        valid = emailValid && valid;

        const usernameValid = this.validateUsername();
        valid = usernameValid && valid;

        const passwordValid = this.validatePasswordStrength();
        valid = passwordValid && valid;

        const confirmValid = this.validatePasswordMatch();
        valid = confirmValid && valid;

        const termsCheckbox = document.getElementById('acceptTerms');
        if (!termsCheckbox || !termsCheckbox.checked) {
            this.setFieldError('acceptTermsError', 'Debes aceptar los terminos y condiciones para crear tu cuenta.');
            valid = false;
        }

        this.collectingSummary = false;

        if (!valid) {
            this.lastValidationSummary = this.buildSummaryMessage('Revisa los campos marcados antes de continuar.');
        } else {
            this.lastValidationSummary = '';
            this.validationSummary = [];
        }

        return valid;
    }


    /**
     * Verifica la estructura general del email.
     */
    validateEmail(fieldId = 'registerEmail', errorId = 'registerEmailError', messages = {}) {
        const emailInput = document.getElementById(fieldId);
        const value = emailInput?.value.trim() || '';
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!value) {
            this.setFieldError(errorId, messages.required || 'Ingresa un correo electronico.');
            return false;
        }

        if (!regex.test(value)) {
            this.setFieldError(errorId, messages.invalid || 'Ingresa un correo electronico valido (ejemplo: usuario@dominio.com).');
            return false;
        }

        this.setFieldError(errorId, '');
        return true;
    }


    /**
     * Valida que el nombre de usuario solo contenga caracteres permitidos.
     */
    validateUsername() {
        const usernameInput = document.getElementById('username');
        const value = usernameInput?.value.trim();
        const regex = /^[a-zA-Z0-9_]{3,30}$/;

        if (!value) {
            this.setFieldError('usernameError', 'Elige un nombre de usuario.');
            return false;
        }

        if (!regex.test(value)) {
            this.setFieldError('usernameError', 'El usuario debe tener 3 a 30 caracteres sin espacios ni simbolos (solo letras, numeros o guion bajo).');
            return false;
        }

        this.setFieldError('usernameError', '');
        return true;
    }


    /**
     * Comprueba los requisitos mínimos de seguridad exigidos por el backend.
     */
    validatePasswordStrength() {
        const passwordInput = document.getElementById('registerPassword');
        const value = passwordInput?.value || '';
        const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{16,}$/;

        if (!value) {
            this.setFieldError('registerPasswordError', 'Ingresa una contrasena.');
            this.updatePasswordStrength('');
            return false;
        }

        if (!regex.test(value)) {
            this.setFieldError('registerPasswordError', 'La contrasena debe tener 16 caracteres como minimo e incluir mayusculas, minusculas, numeros y un simbolo (@$!%*?&).');
            this.updatePasswordStrength(value);
            return false;
        }

        this.updatePasswordStrength(value);
        this.setFieldError('registerPasswordError', '');
        return true;
    }


    /**
     * Informa cuando las contraseñas difieren.
     */
    validatePasswordMatch() {
        const password = document.getElementById('registerPassword')?.value;
        const confirm = document.getElementById('confirmPassword')?.value;

        if (!confirm) {
            this.setFieldError('confirmPasswordError', 'Confirma tu contrasena para continuar.');
            return false;
        }

        if (password !== confirm) {
            this.setFieldError('confirmPasswordError', 'Las contrasenas deben ser iguales.');
            return false;
        }

        this.setFieldError('confirmPasswordError', '');
        return true;
    }


    /**
     * Muestra un texto indicativo de la fuerza de la contraseña.
     */
    updatePasswordStrength(value = '') {
        if (!this.passwordStrength) {
            return;
        }

        if (!value) {
            this.passwordStrength.textContent = '';
            this.passwordStrength.className = 'password-strength';
            return;
        }

        const lengthScore = value.length >= 16 ? 1 : 0;
        const complexityScore = /[A-Z]/.test(value) && /[a-z]/.test(value) && /\d/.test(value) && /[@$!%*?&]/.test(value) ? 1 : 0;
        const score = lengthScore + complexityScore;

        if (score === 2) {
            this.passwordStrength.textContent = 'Contrasena segura';
            this.passwordStrength.className = 'password-strength strong';
        } else if (score === 1) {
            this.passwordStrength.textContent = 'Contrasena debil (agrega mas complejidad)';
            this.passwordStrength.className = 'password-strength medium';
        } else {
            this.passwordStrength.textContent = 'Contrasena muy debil';
            this.passwordStrength.className = 'password-strength weak';
        }
    }

    /**
     * Genera un resumen legible con los mensajes acumulados.
     */
    buildSummaryMessage(defaultMessage) {
        const uniqueMessages = Array.from(new Set(this.validationSummary.filter(Boolean)));
        return uniqueMessages.length ? uniqueMessages.join(' ') : defaultMessage;
    }


    /**
     * Activa o desactiva el overlay de carga global.
     */
    toggleLoading(show) {
        if (!this.loadingOverlay) {
            return;
        }

        this.loadingOverlay.style.display = show ? 'flex' : 'none';
    }

    /**
     * Muestra una notificación contextual en pantalla.
     */
    showNotification(message, type = 'info') {
        if (!this.notification) {
            return;
        }

        const iconElement = this.notification.querySelector('.notification-icon');
        const messageElement = this.notification.querySelector('.notification-message');

        const icons = {
            success: '✔',
            error: '✖',
            warning: '⚠',
            info: 'ℹ',
        };

        if (messageElement) {
            messageElement.textContent = message;
        }

        if (iconElement) {
            iconElement.textContent = icons[type] || icons.info;
        }

        this.notification.className = `notification ${type}`;
        this.notification.style.display = 'flex';

        clearTimeout(this.notificationTimeout);
        this.notificationTimeout = setTimeout(() => this.hideNotification(), 5000);
    }

    /**
     * Oculta la notificación en pantalla.
     */
    hideNotification() {
        if (this.notification) {
            this.notification.style.display = 'none';
        }
    }

    /**
     * Setea el mensaje de error en un campo específico.
     */
    setFieldError(fieldId, message) {
        const element = document.getElementById(fieldId);
        if (element) {
            element.textContent = message;
        }

        if (message && this.collectingSummary) {
            this.validationSummary.push(message);
        }

        if (fieldId.endsWith('Error')) {
            const inputId = fieldId.replace(/Error$/, '');
            const field = document.getElementById(inputId);
            if (field) {
                if (message) {
                    field.setAttribute('aria-invalid', 'true');
                } else {
                    field.removeAttribute('aria-invalid');
                }
            }
        }
    }


    /**
     * Limpia los mensajes de error previos.
     */
    clearErrors() {
        const errorIds = [
            'firstNameError',
            'lastNameError',
            'registerEmailError',
            'usernameError',
            'registerPasswordError',
            'confirmPasswordError',
            'acceptTermsError',
            'loginEmailError',
            'loginPasswordError',
        ];

        errorIds.forEach((id) => {
            // Limpiamos el texto y el marcado aria-invalid asociado a cada input.
            const element = document.getElementById(id);
            if (element) {
                element.textContent = '';
            }
            const relatedField = document.getElementById(id.replace(/Error$/, ''));
            relatedField?.removeAttribute('aria-invalid');
        });

        // Reiniciamos el resumen en cada validacion completa.
        this.validationSummary = [];
        this.collectingSummary = false;
        this.lastValidationSummary = '';
    }
}

// Funciones globales

// Funciones globales utilizadas desde el HTML
function showLogin() {
    window.authManager?.showLoginForm();
}

function showRegister() {
    window.authManager?.showRegisterForm();
}

function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    if (!input) {
        return;
    }

    const toggleIcon = input.parentElement?.querySelector('.password-toggle-icon');
    const isPassword = input.type === 'password';
    input.type = isPassword ? 'text' : 'password';

    if (toggleIcon) {
        toggleIcon.textContent = isPassword ? '🙈' : '👁️';
    }
}

function hideNotification() {
    window.authManager?.hideNotification();
}

// Inicializa el gestor cuando el DOM está listo
window.addEventListener('DOMContentLoaded', () => {
    window.authManager = new AuthManager();

    // Efecto de aparición para tarjetas destacadas
    const revealElements = document.querySelectorAll('.feature-card, .testimonial-card');
    const observer = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
                entry.target.style.transition = 'all 0.6s ease';
            }
        });
    }, { threshold: 0.1 });

    revealElements.forEach((element) => {
        element.style.opacity = '0';
        element.style.transform = 'translateY(30px)';
        observer.observe(element);
    });
});

// Exporta la clase para pruebas unitarias si fuese necesario
if (typeof module !== 'undefined' && module.exports) {
    module.exports = AuthManager;
}
