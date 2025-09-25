from flask import Flask, render_template, jsonify, request, redirect, url_for, session, flash
import json
import os
from datetime import datetime
import hashlib
import secrets

app = Flask(__name__)
app.secret_key = secrets.token_hex(16)  # Clave secreta para las sesiones

# Configuración
DATABASE_FILE = 'projects_database.json'
USERS_FILE = 'users_database.json'

# Estructura base de la base de datos de usuarios
def initialize_users_database():
    """Inicializar la base de datos de usuarios"""
    users_data = {
        "users": []
    }
    
    with open(USERS_FILE, 'w', encoding='utf-8') as f:
        json.dump(users_data, f, ensure_ascii=False, indent=2)
    
    return users_data

# Estructura base de la base de datos de proyectos
def initialize_database():
    """Inicializar la base de datos con datos de ejemplo"""
    projects_data = {
        "projects": []
    }
    
    with open(DATABASE_FILE, 'w', encoding='utf-8') as f:
        json.dump(projects_data, f, ensure_ascii=False, indent=2)
    
    return projects_data

def load_database():
    """Cargar la base de datos desde el archivo JSON"""
    if not os.path.exists(DATABASE_FILE):
        projects_data = {}
        return projects_data
    
    try:
        with open(DATABASE_FILE, 'r', encoding='utf-8') as f:
            return json.load(f)
    except (json.JSONDecodeError, FileNotFoundError):
        projects_data = {}
        return projects_data

def load_users_database():
    """Cargar la base de datos de usuarios desde el archivo JSON"""
    if not os.path.exists(USERS_FILE):
        return initialize_users_database()
    
    try:
        with open(USERS_FILE, 'r', encoding='utf-8') as f:
            return json.load(f)
    except (json.JSONDecodeError, FileNotFoundError):
        return initialize_users_database()

def save_database(data):
    """Guardar los datos en el archivo JSON"""
    with open(DATABASE_FILE, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

def save_users_database(data):
    """Guardar los datos de usuarios en el archivo JSON"""
    with open(USERS_FILE, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

def hash_password(password):
    """Encriptar contraseña usando SHA-256"""
    return hashlib.sha256(password.encode()).hexdigest()

def verify_password(password, hashed_password):
    """Verificar contraseña"""
    return hash_password(password) == hashed_password

# Rutas de autenticación
@app.route('/')
def index():
    """Página de login/registro"""
    return render_template('index.html')

@app.route('/api/register', methods=['POST'])
def register():
    """Registrar nuevo usuario"""
    try:
        data = request.get_json()
        
        # Validar datos requeridos
        required_fields = ['firstName', 'lastName', 'email', 'username', 'password']
        for field in required_fields:
            if field not in data or not data[field]:
                return jsonify({'error': f'El campo {field} es requerido'}), 400
        
        users_db = load_users_database()
        
        # Verificar si el email ya existe
        if any(user['email'] == data['email'] for user in users_db['users']):
            return jsonify({'error': 'El email ya está registrado'}), 400
        
        # Verificar si el username ya existe
        if any(user['username'] == data['username'] for user in users_db['users']):
            return jsonify({'error': 'El nombre de usuario ya está en uso'}), 400
        
        # Crear nuevo usuario
        new_user = {
            'id': len(users_db['users']) + 1,
            'firstName': data['firstName'],
            'lastName': data['lastName'],
            'email': data['email'],
            'username': data['username'],
            'password': hash_password(data['password']),
            'skills': data.get('skills', '').split(',') if data.get('skills') else [],
            'age': data.get('age', ''),
            'birthDate': data.get('birthDate', ''),
            'languages': data.get('languages', ''),
            'specialization': data.get('specialization', ''),
            'phone': data.get('phone', ''),
            'linkedin': data.get('linkedin', ''),
            'github': data.get('github', ''),
            'portfolio': data.get('portfolio', ''),
            'bio': data.get('bio', ''),
            'status': 'Disponible',
            'certifications': data.get('certifications', []),
            'interests': data.get('interests', []),
            'created_at': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
            'updated_at': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        }
        
        users_db['users'].append(new_user)
        save_users_database(users_db)
        
        # Iniciar sesión automáticamente
        session['user_id'] = new_user['id']
        session['username'] = new_user['username']
        
        return jsonify({
            'message': 'Usuario registrado exitosamente',
            'user': {
                'id': new_user['id'],
                'username': new_user['username'],
                'firstName': new_user['firstName'],
                'lastName': new_user['lastName']
            }
        }), 201
        
    except Exception as e:
        return jsonify({'error': f'Error en el registro: {str(e)}'}), 500

@app.route('/api/login', methods=['POST'])
def login():
    """Iniciar sesión"""
    try:
        data = request.get_json()
        
        if not data.get('email') or not data.get('password'):
            return jsonify({'error': 'Email y contraseña son requeridos'}), 400
        
        users_db = load_users_database()
        
        # Buscar usuario por email
        user = next((u for u in users_db['users'] if u['email'] == data['email']), None)
        
        if not user:
            return jsonify({'error': 'Email o contraseña incorrectos'}), 401
        
        # Verificar contraseña
        if not verify_password(data['password'], user['password']):
            return jsonify({'error': 'Email o contraseña incorrectos'}), 401
        
        # Iniciar sesión
        session['user_id'] = user['id']
        session['username'] = user['username']
        
        return jsonify({
            'message': 'Inicio de sesión exitoso',
            'user': {
                'id': user['id'],
                'username': user['username'],
                'firstName': user['firstName'],
                'lastName': user['lastName']
            }
        }), 200
        
    except Exception as e:
        return jsonify({'error': f'Error en el login: {str(e)}'}), 500

@app.route('/api/logout', methods=['POST'])
def logout():
    """Cerrar sesión"""
    session.clear()
    return jsonify({'message': 'Sesión cerrada exitosamente'}), 200

@app.route('/api/user/profile')
def get_user_profile():
    """Obtener perfil del usuario logueado"""
    if 'user_id' not in session:
        return jsonify({'error': 'No hay sesión activa'}), 401
    
    users_db = load_users_database()
    user = next((u for u in users_db['users'] if u['id'] == session['user_id']), None)
    
    if not user:
        return jsonify({'error': 'Usuario no encontrado'}), 404
    
    # Remover la contraseña del response
    user_profile = {k: v for k, v in user.items() if k != 'password'}
    return jsonify(user_profile)

@app.route('/api/user/profile', methods=['PUT'])
def update_user_profile():
    """Actualizar perfil del usuario logueado"""
    if 'user_id' not in session:
        return jsonify({'error': 'No hay sesión activa'}), 401
    
    try:
        data = request.get_json()
        users_db = load_users_database()
        
        user_index = next((i for i, u in enumerate(users_db['users']) if u['id'] == session['user_id']), None)
        
        if user_index is None:
            return jsonify({'error': 'Usuario no encontrado'}), 404
        
        # Actualizar campos permitidos
        allowed_fields = ['firstName', 'lastName', 'age', 'birthDate', 'languages', 
                         'specialization', 'phone', 'linkedin', 'github', 'portfolio', 
                         'bio', 'skills', 'certifications', 'interests']
        
        for field in allowed_fields:
            if field in data:
                if field == 'skills' and isinstance(data[field], str):
                    users_db['users'][user_index][field] = [s.strip() for s in data[field].split(',') if s.strip()]
                else:
                    users_db['users'][user_index][field] = data[field]
        
        users_db['users'][user_index]['updated_at'] = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        save_users_database(users_db)
        
        # Remover la contraseña del response
        user_profile = {k: v for k, v in users_db['users'][user_index].items() if k != 'password'}
        return jsonify(user_profile)
        
    except Exception as e:
        return jsonify({'error': f'Error actualizando perfil: {str(e)}'}), 500

# Rutas protegidas
@app.route('/feed')
def feed():
    """Feed de proyectos - requiere autenticación"""
    if 'user_id' not in session:
        return redirect(url_for('index'))
    return render_template('feed.html')

@app.route('/bio')
def bio():
    """Perfil de usuario - requiere autenticación"""
    if 'user_id' not in session:
        return redirect(url_for('index'))
    return render_template('Bio.html')

# Rutas de proyectos (existentes)
@app.route('/api/projects')
def get_projects():
    """Obtener todos los proyectos"""
    if 'user_id' not in session:
        return jsonify({'error': 'No autorizado'}), 401
    
    db = load_database()
    return jsonify(db.get('projects', []))

@app.route('/api/projects/<int:project_id>')
def get_project(project_id):
    """Obtener un proyecto específico por ID"""
    if 'user_id' not in session:
        return jsonify({'error': 'No autorizado'}), 401
    
    db = load_database()
    project = next((p for p in db.get('projects', []) if p['id'] == project_id), None)
    if project:
        return jsonify(project)
    return jsonify({'error': 'Proyecto no encontrado'}), 404

@app.route('/api/projects', methods=['POST'])
def create_project():
    """Crear un nuevo proyecto"""
    if 'user_id' not in session:
        return jsonify({'error': 'No autorizado'}), 401
    
    db = load_database()
    new_project = request.json
    
    # Generar nuevo ID
    max_id = max([p['id'] for p in db.get('projects', [])], default=0)
    new_project['id'] = max_id + 1
    new_project['created_at'] = datetime.now().strftime('%Y-%m-%d')
    new_project['updated_at'] = datetime.now().strftime('%Y-%m-%d')
    
    if 'projects' not in db:
        db['projects'] = []
    
    db['projects'].append(new_project)
    save_database(db)
    
    return jsonify(new_project), 201

@app.route('/api/projects/<int:project_id>', methods=['PUT'])
def update_project(project_id):
    """Actualizar un proyecto existente"""
    if 'user_id' not in session:
        return jsonify({'error': 'No autorizado'}), 401
    
    db = load_database()
    project_index = next((i for i, p in enumerate(db.get('projects', [])) if p['id'] == project_id), None)
    
    if project_index is not None:
        updated_data = request.json
        updated_data['id'] = project_id
        updated_data['updated_at'] = datetime.now().strftime('%Y-%m-%d')
        
        db['projects'][project_index].update(updated_data)
        save_database(db)
        
        return jsonify(db['projects'][project_index])
    
    return jsonify({'error': 'Proyecto no encontrado'}), 404

@app.route('/api/projects/<int:project_id>', methods=['DELETE'])
def delete_project(project_id):
    """Eliminar un proyecto"""
    if 'user_id' not in session:
        return jsonify({'error': 'No autorizado'}), 401
    
    db = load_database()
    project_index = next((i for i, p in enumerate(db.get('projects', [])) if p['id'] == project_id), None)
    
    if project_index is not None:
        deleted_project = db['projects'].pop(project_index)
        save_database(db)
        return jsonify({'message': 'Proyecto eliminado', 'project': deleted_project})
    
    return jsonify({'error': 'Proyecto no encontrado'}), 404

@app.route('/api/projects/search')
def search_projects():
    """Buscar proyectos por título, tecnología o habilidad"""
    if 'user_id' not in session:
        return jsonify({'error': 'No autorizado'}), 401
    
    query = request.args.get('q', '').lower()
    db = load_database()
    
    filtered_projects = []
    for project in db.get('projects', []):
        # Buscar en título
        if query in project['title'].lower():
            filtered_projects.append(project)
            continue
        
        # Buscar en tecnologías
        for tech in project.get('technologies', []):
            if query in tech['name'].lower():
                filtered_projects.append(project)
                break
        else:
            # Buscar en habilidades necesarias
            for skill in project.get('skills_needed', []):
                if query in skill.lower():
                    filtered_projects.append(project)
                    break
    
    return jsonify(filtered_projects)

if __name__ == '__main__':
    # Inicializar las bases de datos si no existen
    if not os.path.exists(DATABASE_FILE):
        initialize_database()
        print(f"Base de datos de proyectos inicializada en {DATABASE_FILE}")
    
    if not os.path.exists(USERS_FILE):
        initialize_users_database()
        print(f"Base de datos de usuarios inicializada en {USERS_FILE}")
    
    app.run(debug=True, host='0.0.0.0', port=5000)