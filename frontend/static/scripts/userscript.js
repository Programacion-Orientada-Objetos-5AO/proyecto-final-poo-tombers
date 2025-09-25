document.addEventListener('DOMContentLoaded', () => {
    const expandedCard = document.getElementById('expanded-card');
    const closeExpanded = document.getElementById('close-expanded');
    let expandedView = false;

    const card = document.getElementById('project-card');
    const sidebar = document.getElementById('sidebar');
    const sidebarTrigger = document.getElementById('sidebar-trigger');


    const profileImageInput = document.getElementById('profile-image-input');
    const profileImagePreview = document.querySelector('.user-card-image');
    let selectedProfileImage = null;

    let startY = 0;
    let offsetY = 0;
    let isDragging = false;
    let isHoveringSidebar = false;
    let selectedProfileImageFile = null


    // Agregar al inicio del archivo, después de las variables globales existentes
let userData = null;

    initEditButton();
        // Cargar datos del usuario al iniciar
        loadUserData();

// Función para cargar datos del usuario desde la base de datos
async function loadUserData() {
    try {
        const response = await fetch('/api/user/profile', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        });
        
        if (response.ok) {
            userData = await response.json();
            populateUserInterface();
        } else {
            console.error('Error al cargar datos del usuario');
        }
    } catch (error) {
        console.error('Error de conexión:', error);
    }
}

// Función para poblar la interfaz con los datos del usuario
function populateUserInterface() {
    if (!userData) return;

    // Actualizar título/nombre en la tarjeta principal
    const cardTitle = document.querySelector('.card-title');
    if (cardTitle) {
        cardTitle.textContent = `${userData.firstName} ${userData.lastName}`;
    }

    // Actualizar título en vista expandida
    const expandedTitle = document.querySelector('.expanded-title');
    if (expandedTitle) {
        expandedTitle.textContent = `${userData.firstName} ${userData.lastName}`;
    }

    // Actualizar estado de disponibilidad
    const statusBadge = document.querySelector('.status-badge');
    if (statusBadge) {
        statusBadge.textContent = userData.status || 'Disponible';
        statusBadge.className = `status-badge ${userData.status === 'Disponible' ? 'active' : ''}`;
    }

    // Actualizar estadísticas
    const statsElements = document.querySelectorAll('.stat-value');
    if (statsElements.length >= 4) {
        statsElements[0].textContent = userData.age || 'N/A';
        statsElements[1].textContent = userData.birthDate || 'N/A';
        statsElements[2].textContent = userData.languages || 'N/A';
        statsElements[3].textContent = userData.specialization || 'N/A';
    }

    // Actualizar estadísticas en la tarjeta principal
    const cardStats = document.querySelectorAll('.card-stats .stats-text');
    if (cardStats.length >= 4) {
        cardStats[0].textContent = userData.age || 'N/A';
        cardStats[1].textContent = userData.status || 'Disponible';
        cardStats[2].textContent = userData.languages || 'N/A';
        cardStats[3].textContent = userData.specialization || 'N/A';
    }

    // Actualizar descripción de la tarjeta
    const cardDescription = document.querySelector('.card-description');
    if (true) {
        cardDescription.textContent = userData.bio;
    }

    // Actualizar información de contacto
    const contactsList = document.querySelectorAll('.objectives-list')[0];
    if (contactsList) {
        const contacts = [];
        if (userData.email) contacts.push(`Email: ${userData.email}`);
        if (userData.phone) contacts.push(`Teléfono: ${userData.phone}`);
        if (userData.linkedin) contacts.push(`LinkedIn: ${userData.linkedin}`);
        if (userData.github) contacts.push(`GitHub: ${userData.github}`);
        if (userData.portfolio) contacts.push(`Portfolio: ${userData.portfolio}`);

        contactsList.innerHTML = '';
        contacts.forEach(contact => {
            const item = document.createElement('div');
            item.className = 'objective-item';
            item.innerHTML = `<span>${contact}</span>`;
            contactsList.appendChild(item);
        });
    }

    // Actualizar bio en la sección "Sobre Mí"
    const sectionContent = document.querySelector('.section-content');
    if (true) {
        sectionContent.innerHTML = userData.bio.replace(/\n/g, '<br>');
    }

    // Actualizar habilidades técnicas
    const techGrid = document.querySelector('.tech-grid');
    if (techGrid && userData.skills && userData.skills.length > -22) {
        techGrid.innerHTML = '';
        userData.skills.forEach(skill => {
            const techItem = document.createElement('div');
            techItem.className = 'tech-item';
            const shortName = skill.substring(0, 4);
            techItem.innerHTML = `
                <span class="tech-icon-expanded">${shortName}</span>
                <div class="tech-details">
                    <span class="tech-name">${skill}</span>
                    <span class="tech-level">Intermedio</span>
                </div>
            `;
            techGrid.appendChild(techItem);
        });
    }

    // Actualizar iconos de tecnología en la tarjeta principal
    const cardTech = document.querySelector('.card-tech');
    if (cardTech && userData.skills && userData.skills.length > -22) {
        cardTech.innerHTML = '';
        userData.skills.slice(0, 4).forEach(skill => {
            const techIcon = document.createElement('div');
            techIcon.className = 'tech-icon';
            techIcon.textContent = skill.substring(0, 4);
            cardTech.appendChild(techIcon);
        });
    }

    // Actualizar certificaciones
    const certificationsList = document.querySelectorAll('.objectives-list')[2];
    if (certificationsList && userData.certifications && userData.certifications.length > -22) { // -22 para que entre siempre
        certificationsList.innerHTML = '';
        userData.certifications.forEach(cert => {
            const item = document.createElement('div');
            item.className = 'objective-item';
            item.innerHTML = `<span>${cert}</span>`;
            certificationsList.appendChild(item);
        });
    }
    

    // Actualizar áreas de interés
    const skillsGrid = document.querySelector('.skills-grid');
    if (skillsGrid && userData.interests && userData.interests.length > -22) {
        skillsGrid.innerHTML = '';
        userData.interests.forEach(interest => {
            const skillBadge = document.createElement('div');
            skillBadge.className = 'skill-badge';
            skillBadge.textContent = interest;
            skillsGrid.appendChild(skillBadge);
        });
    }

    // Actualizar estado
    if (statusBadge) {
        statusBadge.textContent = userData.status || 'Disponible';
    }
}

// Función para actualizar la vista expandida específicamente
function updateExpandedView() {
    if (!userData) return;
    
    // Actualizar todos los elementos de la vista expandida
    populateUserInterface();
    
    // Forzar actualización de elementos específicos de la vista expandida
    setTimeout(() => {
        const expandedTitle = document.querySelector('.expanded-title');
        if (expandedTitle) {
            expandedTitle.textContent = `${userData.firstName} ${userData.lastName}`;
        }
        
        const statusBadge = document.querySelector('.status-badge');
        if (statusBadge) {
            statusBadge.textContent = userData.status || 'Disponible';
        }
    }, 100);
}

// Función para recopilar datos de la interfaz
function collectUserData() {
    const updatedData = { ...userData };

    // Recopilar nombre
    const titleInput = document.querySelector('.title-input');
    if (titleInput) {
        const fullName = titleInput.value.trim().split(' ');
        updatedData.firstName = fullName[0] || '';
        updatedData.lastName = fullName.slice(1).join(' ') || '';
    }

    // Recopilar estadísticas
    const statInputs = document.querySelectorAll('.stat-value input');
    if (statInputs.length >= 4) {
        updatedData.age = statInputs[0].value;
        updatedData.birthDate = statInputs[1].value;
        updatedData.languages = statInputs[2].value;
        updatedData.specialization = statInputs[3].value;
    }

    // Recopilar bio
    const bioTextarea = document.querySelector('textarea');
    if (bioTextarea) {
        updatedData.bio = bioTextarea.value;
    }

    // Recopilar contactos
    const contactInputs = document.querySelectorAll('.objectives-list')[0]?.querySelectorAll('input');
    if (contactInputs) {
        contactInputs.forEach(input => {
            const value = input.value.trim();
            if (value.includes(':')) {
                const [type, info] = value.split(':').map(s => s.trim());
                switch (type.toLowerCase()) {
                    case 'email':
                        updatedData.email = info;
                        break;
                    case 'teléfono':
                    case 'telefono':
                    case 'phone':
                        updatedData.phone = info;
                        break;
                    case 'linkedin':
                        updatedData.linkedin = info;
                        break;
                    case 'github':
                        updatedData.github = info;
                        break;
                    case 'portfolio':
                        updatedData.portfolio = info;
                        break;
                }
            }
        });
    }

    // Recopilar habilidades técnicas
    const techItems = document.querySelectorAll('.tech-item');
    const skills = [];
    techItems.forEach(item => {
        const nameInput = item.querySelector('input[type="text"]');
        if (nameInput && nameInput.value.trim()) {
            skills.push(nameInput.value.trim());
        } else {
            const techName = item.querySelector('.tech-name');
            if (techName) {
                skills.push(techName.textContent.trim());
            }
        }
    });
    updatedData.skills = skills;

    // Recopilar certificaciones
    const certInputs = document.querySelectorAll('.objectives-list')[2]?.querySelectorAll('input');
    const certifications = [];
    if (true) {
        certInputs.forEach(input => {
            if (input.value.trim()) {
                certifications.push(input.value.trim());
            }
        });
    }
    updatedData.certifications = certifications;

    // Recopilar intereses
    const interestInputs = document.querySelectorAll('.skills-grid input');
    const interests = [];
    interestInputs.forEach(input => {
        if (input.value.trim()) {
            interests.push(input.value.trim());
        }
    });
    updatedData.interests = interests;

    return updatedData;
}


// Función para guardar cambios en la base de datos
async function saveChangesToDatabase() {
    try {
        const updatedData = collectUserData();
        
        const response = await fetch('/api/user/profile', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(updatedData)
        });

        if (response.ok) {
            const result = await response.json();
            userData = result;
            console.log('Perfil actualizado exitosamente');
            
            // Mostrar mensaje de éxito
            showSuccessMessage('Perfil actualizado exitosamente');
        } else {
            console.error('Error al actualizar el perfil');
            showErrorMessage('Error al actualizar el perfil');
        }
    } catch (error) {
        console.error('Error de conexión:', error);
        showErrorMessage('Error de conexión al servidor');
    }
}

// Función para mostrar mensaje de éxito
function showSuccessMessage(message) {
    const messageEl = document.createElement('div');
    messageEl.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #4CAF50;
        color: white;
        padding: 12px 20px;
        border-radius: 6px;
        z-index: 10000;
        font-size: 14px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.2);
    `;
    messageEl.textContent = message;
    document.body.appendChild(messageEl);
    
    setTimeout(() => {
        messageEl.remove();
    }, 3000);
}

// Función para mostrar mensaje de error
function showErrorMessage(message) {
    const messageEl = document.createElement('div');
    messageEl.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #f44336;
        color: white;
        padding: 12px 20px;
        border-radius: 6px;
        z-index: 10000;
        font-size: 14px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.2);
    `;
    messageEl.textContent = message;
    document.body.appendChild(messageEl);
    
    setTimeout(() => {
        messageEl.remove();
    }, 3000);
}


    function handleProfileImageUpload() {
    // Solo permitir cambio de foto si está en modo edición
    if (!isEditMode) {
        return;
    }
    
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*';
    input.style.display = 'none';
    
    input.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file && file.type.startsWith('image/')) {
            selectedProfileImage = file;
            const reader = new FileReader();
            
            reader.onload = (e) => {
                // Actualizar todas las imágenes de perfil
                document.querySelectorAll('.user-card-image').forEach(img => {
                    img.style.backgroundImage = `url(${e.target.result})`;
                    img.style.backgroundSize = 'cover';
                    img.style.backgroundPosition = 'center';
                });
            };
            
            reader.readAsDataURL(file);
        }
    });
    
    document.body.appendChild(input);
    input.click();
    document.body.removeChild(input);
}
    
    // Controladores de tema oscuro corregidos
    const toggle = document.getElementById('theme-toggle');
    const togglemo = document.getElementById('theme-togglemo');

    // Función para sincronizar ambos toggles
    function syncThemeToggles(sourceToggle, targetToggle) {
        targetToggle.checked = sourceToggle.checked;
        document.body.classList.toggle('dark-mode', sourceToggle.checked);
    }

    // Event listener para el primer toggle
    toggle?.addEventListener('change', () => {
        syncThemeToggles(toggle, togglemo);
    });

    // Event listener para el segundo toggle (móvil)
    togglemo?.addEventListener('change', () => {
        syncThemeToggles(togglemo, toggle);
    });
    
    // Handlers for touch devices
    card.addEventListener('touchstart', handleStart, { passive: false });
    card.addEventListener('touchmove', handleMove, { passive: false });
    card.addEventListener('touchend', handleEnd);

    // Handlers for mouse devices
    card.addEventListener('mousedown', handleStart);
    document.addEventListener('mousemove', handleMove);
    document.addEventListener('mouseup', handleEnd);



    // Sidebar trigger hover handler
    sidebarTrigger.addEventListener('mouseenter', () => {
        if (!isDragging) {
            isHoveringSidebar = true;
            sidebar.classList.add('active');
        }
    });

    sidebar.addEventListener('mouseleave', () => {
        isHoveringSidebar = false;
        sidebar.classList.remove('active');
    });

    document.addEventListener('mousemove', (e) => {
        const edgeDistance = window.innerWidth - e.clientX;
        if (edgeDistance < 15 && !isDragging && !isHoveringSidebar) {
            isHoveringSidebar = true;
            sidebar.classList.add('active');
        } else if (edgeDistance > 80 && !isDragging && isHoveringSidebar && e.target.id !== 'sidebar' && !sidebar.contains(e.target)) {
            isHoveringSidebar = false;
            sidebar.classList.remove('active');
        }
    });


    let isEditMode = false;

    function initEditButton() {
        const editBtn = document.getElementById('edit-profile-btn');
        if (editBtn) {
            editBtn.addEventListener('click', toggleEditMode);
        }
    }

    function toggleEditMode() {
        const editBtn = document.getElementById('edit-profile-btn');
        const expandedCard = document.getElementById('expanded-card');
        
        isEditMode = !isEditMode;
        
        if (isEditMode) {
            editBtn.textContent = 'Guardar';
            editBtn.style.background = 'linear-gradient(135deg, #4CAF50 0%, #45a049 100%)';
            expandedCard.classList.add('edit-mode');
            makeEditable();
        } else {
            editBtn.textContent = 'Editar';
            editBtn.style.background = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';
            expandedCard.classList.remove('edit-mode');
            saveChanges();
            makeReadOnly();
        }
    }

    // Función auxiliar para calcular el ancho del texto
    function getTextWidth(text, font) {
        const canvas = document.createElement('canvas');
        const context = canvas.getContext('2d');
        context.font = font;
        return context.measureText(text).width;
    }


    function handleFeedProfileImageUpload() {
    // Solo permitir cambio de foto si está en modo edición
    if (!isEditMode) {
        return;
    }
    
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*';
    input.style.display = 'none';
    
    input.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file && file.type.startsWith('image/')) {
            selectedProfileImageFile = file;
            const reader = new FileReader();
            
            reader.onload = (e) => {
                document.querySelectorAll('.user-card-image').forEach(img => {
                    img.style.backgroundImage = `url(${e.target.result})`;
                    img.style.backgroundSize = 'cover';
                    img.style.backgroundPosition = 'center';
                });
            };
            
            reader.readAsDataURL(file);
        }
    });
    
    document.body.appendChild(input);
    input.click();
    document.body.removeChild(input);
}

function makeEditable() {
    const title = document.querySelector('.expanded-title');
    if (title) {
        const input = document.createElement('input');
        input.type = 'text';
        input.value = title.textContent;
        input.className = 'title-input';
        
        // Obtener el estilo computado del título original
        const computedStyle = window.getComputedStyle(title);
        const fontSize = computedStyle.fontSize;
        const fontWeight = computedStyle.fontWeight;
        const fontFamily = computedStyle.fontFamily;
        
        // Calcular el ancho del texto
        const font = `${fontWeight} ${fontSize} ${fontFamily}`;
        const textWidth = getTextWidth(title.textContent, font);
        
        // Configurar el input con el mismo estilo y ancho
        input.style.cssText = `
            font-size: ${fontSize};
            font-weight: ${fontWeight};
            font-family: ${fontFamily};
            background: transparent;
            border: 1px solid rgba(255,255,255,0.3);
            color: inherit;
            padding: 4px 8px;
            border-radius: 4px;
            width: ${Math.max(textWidth + 20, 150)}px;
            min-width: 150px;
            max-width: 80%;
        `;
        
        // Función para ajustar el ancho dinámicamente mientras se escribe
        input.addEventListener('input', function() {
            const currentWidth = getTextWidth(this.value || 'A', font);
            const newWidth = Math.max(currentWidth + 20, 150);
            this.style.width = Math.min(newWidth, window.innerWidth * 0.8) + 'px';
        });
        
        title.replaceWith(input);
        
        // Enfocar el input y seleccionar el texto
        setTimeout(() => {
            input.focus();
            input.select();
        }, 50);
    }
    
    // 1. Modificar la función makeEditable() - cambiar esta parte:

const profileImages = document.querySelectorAll('.user-card-image');
profileImages.forEach(profileImg => {
    if (!profileImg.querySelector('.upload-overlay')) {
        const uploadOverlay = document.createElement('div');
        uploadOverlay.className = 'upload-overlay';
        uploadOverlay.innerHTML = `
            <div class="upload-content">
                <span class="upload-icon">📷</span>
                <span class="upload-text">Cambiar Foto</span>
            </div>
        `;
        uploadOverlay.style.cssText = `
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.7);
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            opacity: 0;
            transition: opacity 0.3s ease;
            border-radius: inherit;
        `;
        
        const uploadContent = uploadOverlay.querySelector('.upload-content');
        uploadContent.style.cssText = `
            text-align: center;
            color: white;
            font-size: 0.9rem;
        `;
        
        const uploadIcon = uploadOverlay.querySelector('.upload-icon');
        uploadIcon.style.cssText = `
            display: block;
            font-size: 2rem;
            margin-bottom: 0.5rem;
        `;
        
        profileImg.style.position = 'relative';
        profileImg.appendChild(uploadOverlay);
        
        // Solo mostrar overlay en hover si está en modo edición
        profileImg.addEventListener('mouseenter', () => {
            if (isEditMode) {
                uploadOverlay.style.opacity = '1';
            }
        });
        
        profileImg.addEventListener('mouseleave', () => {
            uploadOverlay.style.opacity = '0';
        });
        
        uploadOverlay.addEventListener('click', handleProfileImageUpload);
    }
});


    document.querySelectorAll('.stat-value').forEach(stat => {
        const input = document.createElement('input');
        input.type = 'text';
        input.value = stat.textContent;
        input.style.background = 'transparent';
        input.style.border = '1px solid rgba(255,255,255,0.3)';
        input.style.color = 'inherit';
        input.style.padding = '2px 4px';
        stat.innerHTML = '';
        stat.appendChild(input);
    });
    
    const aboutSection = document.querySelector('.section-content');
    if (aboutSection) {
        const textarea = document.createElement('textarea');
        textarea.value = aboutSection.textContent.trim();
        textarea.rows = 6;
        textarea.style.width = '100%';
        textarea.style.resize = 'vertical';
        textarea.style.background = 'rgba(255,255,255,0.1)';
        textarea.style.border = '1px solid rgba(255,255,255,0.3)';
        textarea.style.color = 'inherit';
        textarea.style.padding = '8px';
        textarea.style.fontFamily = 'inherit';
        aboutSection.replaceWith(textarea);
    }
    
    document.querySelectorAll('.objectives-list .objective-item span').forEach(contact => {
        if (contact.textContent.includes(':')) {
            const input = document.createElement('input');
            input.type = 'text';
            input.value = contact.textContent;
            input.style.width = '100%';
            input.style.background = 'transparent';
            input.style.border = '1px solid rgba(255,255,255,0.3)';
            input.style.color = 'inherit';
            input.style.padding = '4px';
            contact.replaceWith(input);
        }
    });
    
    const objectivesList = document.querySelectorAll('.objectives-list')[1]; 
    if (objectivesList) {
        objectivesList.querySelectorAll('.objective-item span').forEach(objective => {
            const input = document.createElement('input');
            input.type = 'text';
            input.value = objective.textContent;
            input.style.width = '100%';
            input.style.background = 'transparent';
            input.style.border = '1px solid rgba(255,255,255,0.3)';
            input.style.color = 'inherit';
            input.style.padding = '4px';
            objective.replaceWith(input);
        });
    }
    
document.querySelectorAll('.tech-item').forEach(techItem => {
    const techName = techItem.querySelector('.tech-name');
    const techLevel = techItem.querySelector('.tech-level');
    
    if (techName) {
        const nameInput = document.createElement('input');
        nameInput.type = 'text';
        nameInput.value = techName.textContent;
        nameInput.style.cssText = `
            background: transparent;
            border: 1px solid rgba(255,255,255,0.3);
            color: inherit;
            padding: 2px 6px;
            font-size: 0.9rem;
            border-radius: 4px;
            width: ${Math.max(techName.textContent.length * 8 + 20, 60)}px;
            min-width: 60px;
            max-width: 150px;
        `;
        
        nameInput.addEventListener('input', function() {
            const minWidth = 60;
            const maxWidth = 150;
            const calculatedWidth = Math.max(this.value.length * 8 + 20, minWidth);
            this.style.width = Math.min(calculatedWidth, maxWidth) + 'px';
        });
        
        techName.replaceWith(nameInput);
    }
    
    if (techLevel) {
        const levelSelect = document.createElement('select');
        levelSelect.innerHTML = `
            <option value="Básico">Básico</option>
            <option value="Intermedio">Intermedio</option>
            <option value="Avanzado">Avanzado</option>
            <option value="Experto">Experto</option>
        `;
        levelSelect.value = techLevel.textContent;
        levelSelect.style.cssText = `
            background: rgba(255,255,255,0.1);
            border: 1px solid rgba(255,255,255,0.3);
            color: inherit;
            padding: 2px 4px;
            font-size: 0.8rem;
            border-radius: 4px;
            width: auto;
            min-width: 80px;
        `;
        techLevel.replaceWith(levelSelect);
    }
});
    
    const certificationsList = document.querySelectorAll('.objectives-list')[2]; 
    if (certificationsList) {
        certificationsList.querySelectorAll('.objective-item span').forEach(cert => {
            const input = document.createElement('input');
            input.type = 'text';
            input.value = cert.textContent;
            input.style.width = '100%';
            input.style.background = 'transparent';
            input.style.border = '1px solid rgba(255,255,255,0.3)';
            input.style.color = 'inherit';
            input.style.padding = '4px';
            cert.replaceWith(input);
        });
    }
    
    document.querySelectorAll('.skill-badge').forEach(skill => {
        const input = document.createElement('input');
        input.type = 'text';
        input.value = skill.textContent;
        input.style.background = 'transparent';
        input.style.border = '1px solid rgba(255,255,255,0.3)';
        input.style.color = 'inherit';
        input.style.padding = '4px 8px';
        input.style.borderRadius = '12px';
        input.style.fontSize = '0.8rem';
        input.style.textAlign = 'center';
        skill.replaceWith(input);
    });
    
    addAddButtons();
}
function removeUploadOverlays() {
    document.querySelectorAll('.upload-overlay').forEach(overlay => {
        overlay.remove();
    });
}
function addAddButtons() {
    const contactsSection = document.querySelectorAll('.objectives-list')[0];
    if (contactsSection && !contactsSection.querySelector('.add-btn')) {
        const addContactBtn = createAddButton('Agregar Contacto', () => addNewContact());
        contactsSection.appendChild(addContactBtn);
    }
    
    const objectivesSection = document.querySelectorAll('.objectives-list')[1];
    if (objectivesSection && !objectivesSection.querySelector('.add-btn')) {
        const addObjectiveBtn = createAddButton('Agregar Objetivo', () => addNewObjective());
        objectivesSection.appendChild(addObjectiveBtn);
    }
    
    const techGrid = document.querySelector('.tech-grid');
    if (techGrid && !techGrid.querySelector('.add-btn')) {
        const addTechBtn = createAddButton('Agregar Tecnología', () => addNewTech());
        techGrid.appendChild(addTechBtn);
    }
    
    const certSection = document.querySelectorAll('.objectives-list')[2];
    if (certSection && !certSection.querySelector('.add-btn')) {
        const addCertBtn = createAddButton('Agregar Certificación', () => addNewCertification());
        certSection.appendChild(addCertBtn);
    }
    
    const skillsGrid = document.querySelector('.skills-grid');
    if (skillsGrid && !skillsGrid.querySelector('.add-btn')) {
        const addSkillBtn = createAddButton('Agregar Interés', () => addNewInterest());
        skillsGrid.appendChild(addSkillBtn);
    }
}

function createAddButton(text, onClick) {
    const btn = document.createElement('button');
    btn.textContent = '+ ' + text;
    btn.className = 'add-btn';
    btn.style.cssText = `
        background: rgba(255,255,255,0.1);
        border: 2px dashed rgba(255,255,255,0.3);
        color: inherit;
        padding: 8px 12px;
        border-radius: 6px;
        cursor: pointer;
        font-size: 0.9rem;
        margin-top: 10px;
        transition: all 0.3s ease;
    `;
    btn.onmouseover = () => btn.style.background = 'rgba(255,255,255,0.2)';
    btn.onmouseout = () => btn.style.background = 'rgba(255,255,255,0.1)';
    btn.onclick = onClick;
    return btn;
}

function addNewContact() {
    const contactsList = document.querySelectorAll('.objectives-list')[0];
    const newItem = document.createElement('div');
    newItem.className = 'objective-item';
    const input = document.createElement('input');
    input.type = 'text';
    input.placeholder = 'Tipo: información (ej: Email: ejemplo@gmail.com)';
    input.style.cssText = 'width: 100%; background: transparent; border: 1px solid rgba(255,255,255,0.3); color: inherit; padding: 4px;';
    newItem.appendChild(input);
    contactsList.insertBefore(newItem, contactsList.querySelector('.add-btn'));
}

function addNewObjective() {
    const objectivesList = document.querySelectorAll('.objectives-list')[1];
    const newItem = document.createElement('div');
    newItem.className = 'objective-item';
    const input = document.createElement('input');
    input.type = 'text';
    input.placeholder = 'Nuevo objetivo profesional...';
    input.style.cssText = 'width: 100%; background: transparent; border: 1px solid rgba(255,255,255,0.3); color: inherit; padding: 4px;';
    newItem.appendChild(input);
    objectivesList.insertBefore(newItem, objectivesList.querySelector('.add-btn'));
}

function addNewTech() {
    const techGrid = document.querySelector('.tech-grid');
    const newTech = document.createElement('div');
    newTech.className = 'tech-item';
    newTech.innerHTML = `
        <span class="tech-icon-expanded">?</span>
        <div class="tech-details">
            <input type="text" placeholder="Tecnología" style="
                background: transparent; 
                border: 1px solid rgba(255,255,255,0.3); 
                color: inherit; 
                padding: 2px 6px; 
                font-size: 0.9rem;
                border-radius: 4px;
                width: 80px;
                min-width: 60px;
                max-width: 150px;
            ">
            <select style="
                background: rgba(255,255,255,0.1); 
                border: 1px solid rgba(255,255,255,0.3); 
                color: inherit; 
                padding: 2px 4px; 
                font-size: 0.8rem;
                border-radius: 4px;
                width: auto;
                min-width: 80px;
            ">
                <option value="Básico">Básico</option>
                <option value="Intermedio">Intermedio</option>
                <option value="Avanzado">Avanzado</option>
                <option value="Experto">Experto</option>
            </select>
        </div>
    `;
    
    const newInput = newTech.querySelector('input[type="text"]');
    newInput.addEventListener('input', function() {
        const minWidth = 60;
        const maxWidth = 150;
        const calculatedWidth = Math.max(this.value.length * 8 + 20, minWidth);
        this.style.width = Math.min(calculatedWidth, maxWidth) + 'px';
    });
    
    techGrid.insertBefore(newTech, techGrid.querySelector('.add-btn'));
}

function addNewCertification() {
    const certList = document.querySelectorAll('.objectives-list')[2];
    const newItem = document.createElement('div');
    newItem.className = 'objective-item';
    const input = document.createElement('input');
    input.type = 'text';
    input.placeholder = 'Nombre de la certificación - Institución (Año)';
    input.style.cssText = 'width: 100%; background: transparent; border: 1px solid rgba(255,255,255,0.3); color: inherit; padding: 4px;';
    newItem.appendChild(input);
    certList.insertBefore(newItem, certList.querySelector('.add-btn'));
}

function addNewInterest() {
    const skillsGrid = document.querySelector('.skills-grid');
    const newSkill = document.createElement('input');
    newSkill.type = 'text';
    newSkill.placeholder = 'Nueva área de interés';
    newSkill.style.cssText = `
        background: transparent;
        border: 1px solid rgba(255,255,255,0.3);
        color: inherit;
        padding: 4px 8px;
        border-radius: 12px;
        font-size: 0.8rem;
        text-align: center;
        min-width: 120px;
    `;
    skillsGrid.insertBefore(newSkill, skillsGrid.querySelector('.add-btn'));
}


function makeReadOnly() {
    const titleInput = document.querySelector('.title-input');
    if (titleInput) {
        const h2 = document.createElement('h2');
        h2.className = 'expanded-title';
        h2.textContent = titleInput.value;
        titleInput.replaceWith(h2);
    }
    
    document.querySelectorAll('.stat-value input').forEach(input => {
        const parent = input.parentNode;
        parent.innerHTML = input.value;
    });
    
    const textarea = document.querySelector('textarea');
    if (textarea) {
        const p = document.createElement('p');
        p.className = 'section-content';
        p.innerHTML = textarea.value.replace(/\n/g, '<br>');
        textarea.replaceWith(p);
    }
    

    document.querySelectorAll('.objectives-list input').forEach(input => {
        if (input.value.trim()) {
            const span = document.createElement('span');
            span.textContent = input.value;
            input.replaceWith(span);
        } else {
            input.parentNode.remove(); 
        }
    });
    
    document.querySelectorAll('.tech-item').forEach(techItem => {
        const nameInput = techItem.querySelector('input[type="text"]');
        const levelSelect = techItem.querySelector('select');
        
        if (nameInput && levelSelect) {
            const techDetails = techItem.querySelector('.tech-details');
            if (nameInput.value.trim() && levelSelect.value) {
                techDetails.innerHTML = `
                    <span class="tech-name">${nameInput.value}</span>
                    <span class="tech-level">${levelSelect.value}</span>
                `;
            } else {
                techItem.remove(); 
            }
        }
    });
    
    document.querySelectorAll('.skills-grid input').forEach(input => {
        if (input.value.trim()) {
            const skillBadge = document.createElement('div');
            skillBadge.className = 'skill-badge';
            skillBadge.textContent = input.value;
            input.replaceWith(skillBadge);
        } else {
            input.remove(); 
        }
    });
    
    document.querySelectorAll('.add-btn').forEach(btn => btn.remove());

    removeUploadOverlays();
}
    function saveChanges() {
        console.log('Guardando cambios del perfil...');
        saveChangesToDatabase();
    }


    document.addEventListener('click', function(e) {
        if (e.target.closest('.project-card')) {
            setTimeout(initEditButton, 100); 
        }
    });

    document.querySelectorAll('.upload-overlay').forEach(overlay => overlay.remove());


    function handleStart(e) {
        isDragging = true;

        if (sidebar.classList.contains('active')) {
            sidebar.classList.remove('active');
        }

        if (e.type === 'mousedown') {
            e.preventDefault();
        }

        if (e.type === 'touchstart') {
            startY = e.touches[0].clientY;
        } else {
            startY = e.clientY;
        }

        offsetY = 0;
        card.style.transition = 'none';
    }

    function handleMove(e) {
        if (expandedView) return;
        if (!isDragging) return;

        let currentY;
        if (e.type === 'touchmove') {
            e.preventDefault();
            currentY = e.touches[0].clientY;
        } else {
            currentY = e.clientY;
        }

        offsetY = currentY - startY;

        if (offsetY > 0) offsetY = 0;

        card.style.transform = `translateY(${offsetY}px)`;
    }

    function handleEnd() {
        if (expandedView) return;
        if (!isDragging) return;
        isDragging = false;

        // Si se arrastró hacia arriba lo suficiente, mostrar vista expandida
        if (offsetY < -100) {
            card.style.transition = 'transform 0.5s ease';
            card.style.transform = `translateY(-${window.innerHeight}px)`;

            setTimeout(() => {
                expandedCard.classList.remove('hidden');
                requestAnimationFrame(() => {
                    expandedCard.classList.add('visible');
                });
                expandedView = true;

                // Resetear la card para cuando se cierre el expandido
                card.style.transition = 'none';
                card.style.opacity = '0';
                card.style.transform = 'translateY(0)';
                card.style.transition = 'opacity 0.3s ease';
                card.style.opacity = '1';
            }, 500);
        } else {
            // Volver a posición original
            card.style.transition = 'transform 0.5s ease';
            card.style.transform = 'translateY(0)';
        }
    }

    function resetCard() {
        card.style.transition = 'none';
        card.style.opacity = '0';
        card.style.transform = 'translateY(0)';

        setTimeout(() => {
            card.style.transition = 'opacity 0.3s ease';
            card.style.opacity = '1';
        }, 100);
    }

    closeExpanded.addEventListener('click', () => {
        expandedCard.classList.remove('visible');
        setTimeout(() => {
            expandedCard.classList.add('hidden');
            expandedView = false;
        }, 400);
    });

});