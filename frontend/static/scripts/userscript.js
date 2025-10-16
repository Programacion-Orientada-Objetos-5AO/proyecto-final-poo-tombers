// Archivo: static/scripts/userscript.js
// Sincroniza la vista de perfil con el backend de Spring Boot.

class ProfileManager {
    constructor() {
        this.profile = null;
        this.projectCard = document.getElementById('project-card');
        this.expandedCard = document.getElementById('expanded-card');
        this.editButton = document.getElementById('edit-profile-btn');
        this.init();
    }

    resolveAsset(path) {
        if (!path) {
            return null;
        }
        const globalResolver = window.resolveAssetUrl;
        if (typeof globalResolver === 'function') {
            return globalResolver(path);
        }
        if (/^https?:\/\//i.test(path)) {
            return path;
        }
        const base = window.apiClient?.baseUrl ?? '';
        const normalizedBase = base.endsWith('/') ? base.slice(0, -1) : base;
        const normalizedPath = path.startsWith('/') ? path : `/${path}`;
        return `${normalizedBase}${normalizedPath}`;
    }

    async init() {
        if (!window.apiClient || !window.apiClient.auth.isAuthenticated()) {
            window.location.href = '/';
            return;
        }

        await this.loadProfile();
        this.renderProfile();
        this.registerEvents();
    }

    async loadProfile() {
        try {
            this.profile = await window.apiClient.get('/api/users/profile');
        } catch (error) {
            console.error('Error al obtener el perfil:', error);
            this.showToast('No se pudo cargar tu perfil.', 'error');
        }
    }

    registerEvents() {
        const closeExpanded = document.getElementById('close-expanded');
        closeExpanded?.addEventListener('click', () => this.hideExpanded());

        this.projectCard?.addEventListener('click', () => this.showExpanded());
        this.editButton?.addEventListener('click', () => this.openEditModal());
    }

    showExpanded() {
        if (!this.expandedCard) return;
        this.expandedCard.classList.remove('hidden');
        requestAnimationFrame(() => this.expandedCard.classList.add('visible'));
    }

    hideExpanded() {
        if (!this.expandedCard) return;
        this.expandedCard.classList.remove('visible');
        setTimeout(() => this.expandedCard?.classList.add('hidden'), 250);
    }

    renderProfile() {
        if (!this.profile) {
            return;
        }

        const fullName = `${this.profile.firstName || ''} ${this.profile.lastName || ''}`.trim() || 'Usuario sin nombre';
        const status = (this.profile.status || 'DISPONIBLE').toString();

        const pictureUrl = this.resolveAsset(this.profile.profilePictureUrl) || '/static/imagenes/profile-placeholder.svg';
        const cardImage = this.projectCard?.querySelector('.user-card-image');
        if (cardImage) {
            cardImage.style.backgroundImage = `url('${pictureUrl}')`;
            cardImage.style.backgroundSize = 'cover';
            cardImage.style.backgroundPosition = 'center';
            cardImage.style.backgroundRepeat = 'no-repeat';
        }
        const expandedImage = this.expandedCard?.querySelector('.expanded-left .user-card-image');
        if (expandedImage) {
            expandedImage.style.backgroundImage = `url('${pictureUrl}')`;
            expandedImage.style.backgroundSize = 'cover';
            expandedImage.style.backgroundPosition = 'center';
            expandedImage.style.backgroundRepeat = 'no-repeat';
        }

        const cardTitle = this.projectCard?.querySelector('.card-title');
        if (cardTitle) {
            cardTitle.textContent = fullName;
        }

        const expandedTitle = this.expandedCard?.querySelector('.expanded-title');
        if (expandedTitle) {
            expandedTitle.textContent = fullName;
        }

        const statusCircle = this.projectCard?.querySelector('.status-circle');
        if (statusCircle) {
            statusCircle.className = 'status-circle';
            if (status === 'INACTIVO') {
                statusCircle.classList.add('inactive');
            } else if (status === 'OCUPADO') {
                statusCircle.classList.add('ocupado');
            }
        }

        const statusBadge = this.expandedCard?.querySelector('.status-badge');
        if (statusBadge) {
            statusBadge.textContent = this.translateStatus(status);
            statusBadge.className = 'status-badge';
            if (status === 'DISPONIBLE') {
                statusBadge.classList.add('active');
            } else if (status === 'INACTIVO') {
                statusBadge.classList.add('inactive');
            } else if (status === 'OCUPADO') {
                statusBadge.classList.add('ocupado');
            }
        }

        const cardStats = this.projectCard?.querySelectorAll('.stats-text');
        if (cardStats && cardStats.length >= 3) {
            cardStats[0].textContent = this.profile.age?.toString() || 'N/D';
            cardStats[1].textContent = this.profile.languages || 'N/D';
            cardStats[2].textContent = this.profile.specialization || 'N/D';
        }

        const expandedStats = this.expandedCard?.querySelectorAll('.stat-value');
        if (expandedStats && expandedStats.length >= 3) {
            expandedStats[0].textContent = this.profile.age?.toString() || 'N/D';
            expandedStats[1].textContent = this.profile.languages || 'N/D';
            expandedStats[2].textContent = this.profile.specialization || 'N/D';
        }

        const cardDescription = this.projectCard?.querySelector('.card-description');
        if (cardDescription) {
            cardDescription.textContent = this.profile.bio || 'Todavía no completaste tu biografía.';
        }

        const expandedDescription = this.expandedCard?.querySelector('.section-content');
        if (expandedDescription) {
            const formatted = (this.profile.bio || 'Contá quién sos y qué te apasiona.').replace(/\n/g, '<br>');
            expandedDescription.innerHTML = formatted;
        }

        this.renderSkills();
        this.renderCertifications();
        this.renderInterests();
        this.renderContacts();
    }

    renderSkills() {
        const skills = Array.isArray(this.profile?.skills) ? this.profile.skills : [];
        const fallback = skills.length > 0 ? skills : [];

        const cardTech = this.projectCard?.querySelector('.card-tech');
        if (cardTech) {
            cardTech.innerHTML = '';
            fallback.slice(0, 4).forEach((skill) => {
                const badge = document.createElement('div');
                badge.className = 'tech-icon';
                badge.textContent = this.getSkillInitials(skill);
                cardTech.appendChild(badge);
            });
        }

        const techGrid = this.expandedCard?.querySelector('.tech-grid');
        if (techGrid) {
            techGrid.innerHTML = '';
            fallback.forEach((skill) => {
                const item = document.createElement('div');
                item.className = 'tech-item';

                const icon = document.createElement('span');
                icon.className = 'tech-icon-expanded';
                icon.textContent = this.getSkillInitials(skill);

                const details = document.createElement('div');
                details.className = 'tech-details';

                const name = document.createElement('span');
                name.className = 'tech-name';
                const skillName = typeof skill === 'string' ? skill : (skill?.nombre || 'Habilidad');
                name.textContent = skillName;

                const level = document.createElement('span');
                level.className = 'tech-level';
                const nivel = typeof skill === 'object' ? skill.nivel : '';
                level.textContent = nivel ? ` - ${nivel}` : '';

                details.appendChild(name);
                if (nivel) {
                    details.appendChild(level);
                }

                item.appendChild(icon);
                item.appendChild(details);
                techGrid.appendChild(item);
            });
        }
    }

    renderCertifications() {
        const list = this.expandedCard?.querySelector('.objectives-list');
        if (!list) return;

        list.innerHTML = '';
        const certifications = Array.isArray(this.profile?.certifications) ? this.profile.certifications : [];
        if (certifications.length === 0) {
            const empty = document.createElement('div');
            empty.className = 'objective-item';
            empty.textContent = 'Aún no registraste certificaciones.';
            list.appendChild(empty);
            return;
        }

        certifications.forEach((cert) => {
            const item = document.createElement('div');
            item.className = 'objective-item';
            item.textContent = cert;
            list.appendChild(item);
        });
    }

    renderInterests() {
        const grid = this.expandedCard?.querySelector('.skills-grid');
        if (!grid) return;

        grid.innerHTML = '';
        const interests = Array.isArray(this.profile?.interests) ? this.profile.interests : [];
        if (interests.length === 0) {
            const empty = document.createElement('div');
            empty.className = 'skill-badge';
            empty.textContent = 'Sin intereses cargados.';
            grid.appendChild(empty);
            return;
        }

        interests.forEach((interest) => {
            const badge = document.createElement('div');
            badge.className = 'skill-badge';
            badge.textContent = interest;
            grid.appendChild(badge);
        });
    }

    renderContacts() {
        const contactGrid = this.expandedCard?.querySelector('.contact-grid');
        if (!contactGrid) return;

        contactGrid.innerHTML = '';

        const contacts = [
            { label: 'Email', value: this.profile.email, icon: 'mdi--email-outline' },
            { label: 'Teléfono', value: this.profile.phone, icon: 'mdi--phone-outline' },
            { label: 'LinkedIn', value: this.profile.linkedin, icon: 'mdi--linkedin', isLink: true },
            { label: 'GitHub', value: this.profile.github, icon: 'mdi--github', isLink: true },
            { label: 'Portfolio', value: this.profile.portfolio, icon: 'mdi--web', isLink: true }
        ];

        const hasAnyContact = contacts.some(contact => contact.value);

        if (!hasAnyContact) {
            const emptyMessage = document.createElement('div');
            emptyMessage.className = 'contact-item-empty';
            emptyMessage.textContent = 'No hay información de contacto disponible';
            contactGrid.appendChild(emptyMessage);
            return;
        }

        contacts.forEach(contact => {
            if (contact.value) {
                const item = document.createElement('div');
                item.className = 'contact-item';

                const iconWrapper = document.createElement('div');
                iconWrapper.className = 'contact-icon-wrapper';

                const icon = document.createElement('span');
                icon.className = `${contact.icon} contact-icon`;
                iconWrapper.appendChild(icon);

                const content = document.createElement('div');
                content.className = 'contact-content';

                const label = document.createElement('span');
                label.className = 'contact-label';
                label.textContent = contact.label;

                const value = document.createElement('span');
                value.className = 'contact-value';

                if (contact.isLink) {
                    const link = document.createElement('a');
                    link.href = contact.value.startsWith('http') ? contact.value : `https://${contact.value}`;
                    link.target = '_blank';
                    link.rel = 'noopener noreferrer';
                    link.textContent = contact.value;
                    link.className = 'contact-link';
                    value.appendChild(link);
                } else {
                    value.textContent = contact.value;
                }

                content.appendChild(label);
                content.appendChild(value);

                item.appendChild(iconWrapper);
                item.appendChild(content);
                contactGrid.appendChild(item);
            }
        });
    }

    openEditModal() {
        if (!this.profile) {
            return;
        }

        const overlay = document.createElement('div');
        overlay.style.cssText = `
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,0.6);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 10000;
            padding: 16px;
        `;

        const modal = document.createElement('div');
        modal.style.cssText = `
            background: #ffffff;
            width: min(480px, 100%);
            max-height: 90vh;
            overflow-y: auto;
            border-radius: 12px;
            padding: 24px;
            position: relative;
            box-shadow: 0 12px 40px rgba(0,0,0,0.25);
        `;

        const closeBtn = document.createElement('button');
        closeBtn.type = 'button';
        closeBtn.textContent = '×';
        closeBtn.style.cssText = `
            position: absolute;
            top: 8px;
            right: 12px;
            border: none;
            background: transparent;
            font-size: 24px;
            cursor: pointer;
        `;
        closeBtn.addEventListener('click', () => overlay.remove());

        const title = document.createElement('h2');
        title.textContent = 'Editar perfil';
        title.style.marginBottom = '16px';

        const form = document.createElement('form');
        form.style.display = 'flex';
        form.style.flexDirection = 'column';
        form.style.gap = '12px';

        const imageSection = document.createElement('div');
        imageSection.style.display = 'flex';
        imageSection.style.flexDirection = 'column';
        imageSection.style.gap = '8px';

        const imageLabel = document.createElement('span');
        imageLabel.textContent = 'Foto de perfil';
        imageLabel.style.fontSize = '14px';
        imageLabel.style.fontWeight = '500';

        const imagePreview = document.createElement('img');
        imagePreview.src = this.resolveAsset(this.profile?.profilePictureUrl) || '/static/imagenes/profile-placeholder.svg';
        imagePreview.alt = 'Vista previa de la foto de perfil';
        imagePreview.style.width = '96px';
        imagePreview.style.height = '96px';
        imagePreview.style.objectFit = 'cover';
        imagePreview.style.borderRadius = '50%';
        imagePreview.style.border = '1px solid #d0d5dd';

        const imageInput = document.createElement('input');
        imageInput.type = 'file';
        imageInput.name = 'profilePicture';
        imageInput.accept = 'image/*';
        imageInput.style.padding = '10px';
        imageInput.style.borderRadius = '8px';
        imageInput.style.border = '1px solid #d0d5dd';

        imageInput.addEventListener('change', (event) => {
            const file = event.target.files?.[0];
            if (!file) {
                imagePreview.src = this.resolveAsset(this.profile?.profilePictureUrl) || '/static/imagenes/profile-placeholder.svg';
                return;
            }
            const reader = new FileReader();
            reader.onload = (loadEvent) => {
                imagePreview.src = loadEvent.target?.result || imagePreview.src;
            };
            reader.readAsDataURL(file);
        });

        imageSection.appendChild(imageLabel);
        imageSection.appendChild(imagePreview);
        imageSection.appendChild(imageInput);
        form.appendChild(imageSection);

        const fields = [
            { label: 'Nombre', name: 'firstName', type: 'text', value: this.profile.firstName || '', required: true },
            { label: 'Apellido', name: 'lastName', type: 'text', value: this.profile.lastName || '', required: true },
            { label: 'Edad', name: 'age', type: 'number', value: this.profile.age ?? '', min: 0 },
            { label: 'Disponibilidad', name: 'status', type: 'select', value: this.profile.status || 'DISPONIBLE', options: ['DISPONIBLE', 'OCUPADO', 'INACTIVO'] },
            { label: 'Idiomas', name: 'languages', type: 'text', value: this.profile.languages || '' },
            { label: 'Especialización', name: 'specialization', type: 'text', value: this.profile.specialization || '' },
            { label: 'Teléfono', name: 'phone', type: 'text', value: this.profile.phone || '' },
            { label: 'LinkedIn', name: 'linkedin', type: 'text', value: this.profile.linkedin || '', placeholder: 'https://linkedin.com/in/tu-perfil' },
            { label: 'GitHub', name: 'github', type: 'text', value: this.profile.github || '', placeholder: 'https://github.com/tu-usuario' },
            { label: 'Portfolio', name: 'portfolio', type: 'text', value: this.profile.portfolio || '', placeholder: 'https://tu-portfolio.com' },
        ];

        fields.forEach((field) => {
            const wrapper = document.createElement('label');
            wrapper.textContent = field.label;
            wrapper.style.display = 'flex';
            wrapper.style.flexDirection = 'column';
            wrapper.style.fontSize = '14px';
            wrapper.style.fontWeight = '500';
            wrapper.style.gap = '4px';

            if (field.type === 'select') {
                const select = document.createElement('select');
                select.name = field.name;
                select.style.padding = '10px';
                select.style.borderRadius = '8px';
                select.style.border = '1px solid #d0d5dd';
                select.style.fontSize = '14px';
                
                field.options.forEach((option) => {
                    const opt = document.createElement('option');
                    opt.value = option;
                    opt.textContent = this.translateStatus(option);
                    if (option === field.value) {
                        opt.selected = true;
                    }
                    select.appendChild(opt);
                });
                wrapper.appendChild(select);
            } else {
                const input = document.createElement('input');
                input.type = field.type;
                input.name = field.name;
                input.value = field.value;
                if (field.placeholder) input.placeholder = field.placeholder;
                if (field.required) input.required = true;
                if (typeof field.min !== 'undefined') input.min = field.min;
                input.style.padding = '10px';
                input.style.borderRadius = '8px';
                input.style.border = '1px solid #d0d5dd';
                input.style.fontSize = '14px';
                wrapper.appendChild(input);
            }
            form.appendChild(wrapper);
        });

        form.appendChild(this.buildTextarea('Biografía', 'bio', this.profile.bio || '', 4));
        form.appendChild(this.buildTextarea('Certificaciones (una por línea)', 'certifications', (this.profile.certifications || []).join('\n'), 3));
        form.appendChild(this.buildTextarea('Áreas de interés (una por línea)', 'interests', (this.profile.interests || []).join('\n'), 3));
        form.appendChild(this.buildTextarea('Habilidades (formato: nombre - nivel)', 'skills', this.formatSkillsForTextarea(), 3));

        const submit = document.createElement('button');
        submit.type = 'submit';
        submit.textContent = 'Guardar cambios';
        submit.className = 'action-btn primary';
        submit.style.cssText = `
            padding: 12px 24px;
            background: #363F72;
            color: white;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 500;
            align-self: flex-end;
            margin-top: 8px;
        `;
        submit.addEventListener('mouseover', () => {
            submit.style.background = '#2a3159';
        });
        submit.addEventListener('mouseout', () => {
            submit.style.background = '#363F72';
        });

        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            submit.disabled = true;
            submit.textContent = 'Guardando...';
            
            const rawFormData = new FormData(form);
            const payload = this.collectUpdatePayload(rawFormData);

            const request = new FormData();
            request.append('data', new Blob([JSON.stringify(payload)], { type: 'application/json' }));
            const profilePicture = rawFormData.get('profilePicture');
            if (profilePicture instanceof File && profilePicture.size > 0) {
                request.append('profilePicture', profilePicture);
            }

            const success = await this.updateProfile(request);
            
            if (success) {
                overlay.remove();
            } else {
                submit.disabled = false;
                submit.textContent = 'Guardar cambios';
            }
        });

        form.appendChild(submit);
        modal.appendChild(closeBtn);
        modal.appendChild(title);
        modal.appendChild(form);
        overlay.appendChild(modal);
        document.body.appendChild(overlay);
    }

    buildTextarea(label, name, value, rows) {
        const wrapper = document.createElement('label');
        wrapper.textContent = label;
        wrapper.style.display = 'flex';
        wrapper.style.flexDirection = 'column';
        wrapper.style.fontSize = '14px';
        wrapper.style.fontWeight = '500';
        wrapper.style.gap = '4px';

        const textarea = document.createElement('textarea');
        textarea.name = name;
        textarea.rows = rows;
        textarea.value = value;
        textarea.style.padding = '10px';
        textarea.style.borderRadius = '8px';
        textarea.style.border = '1px solid #d0d5dd';
        textarea.style.fontSize = '14px';
        textarea.style.fontFamily = 'inherit';
        textarea.style.resize = 'vertical';

        wrapper.appendChild(textarea);
        return wrapper;
    }

    formatSkillsForTextarea() {
        const skills = Array.isArray(this.profile?.skills) ? this.profile.skills : [];
        return skills.map((skill) => {
            if (typeof skill === 'string') {
                return skill;
            }
            const nombre = skill?.nombre || '';
            const nivel = skill?.nivel || '';
            return nivel ? `${nombre} - ${nivel}` : nombre;
        }).join('\n');
    }

    collectUpdatePayload(formData) {
        const toString = (value) => {
            if (!value) return null;
            const str = value.toString().trim();
            return str === '' ? null : str;
        };

        const certifications = this.splitByLine(formData.get('certifications'));
        const interests = this.splitByLine(formData.get('interests'));
        const skills = this.splitByLine(formData.get('skills')).map((raw) => {
            const parts = raw.split('-').map((part) => part.trim());
            const nombre = parts[0];
            const nivel = parts[1] || 'Intermedio';
            if (!nombre) return null;
            return { nombre, nivel };
        }).filter(Boolean);

        return {
            firstName: toString(formData.get('firstName')),
            lastName: toString(formData.get('lastName')),
            age: this.parseNumber(formData.get('age')),
            status: toString(formData.get('status')) || 'DISPONIBLE',
            languages: toString(formData.get('languages')),
            specialization: toString(formData.get('specialization')),
            phone: toString(formData.get('phone')),
            linkedin: toString(formData.get('linkedin')),
            github: toString(formData.get('github')),
            portfolio: toString(formData.get('portfolio')),
            bio: toString(formData.get('bio')),
            certifications,
            interests,
            skills,
        };
    }

    splitByLine(value) {
        if (!value) return [];
        return value
            .toString()
            .split(/\r?\n/)
            .map((item) => item.trim())
            .filter(Boolean);
    }

    parseNumber(value) {
        if (!value) return null;
        const str = value.toString().trim();
        if (str === '') return null;
        const parsed = Number(str);
        return Number.isNaN(parsed) ? null : parsed;
    }

    async updateProfile(formData) {
        try {
            const updated = await window.apiClient.put('/api/users/profile', formData);
            this.profile = updated;
            this.renderProfile();
            this.showToast('Perfil actualizado correctamente.', 'success');
            return true;
        } catch (error) {
            console.error('Error al actualizar el perfil:', error);
            const message = error?.message || 'No se pudo guardar el perfil.';
            this.showToast(message, 'error');
            return false;
        }
    }

    getSkillInitials(skill) {
        const text = typeof skill === 'string' ? skill : skill?.nombre;
        if (!text) {
            return '---';
        }
        return text
            .split(/\s+/)
            .filter(Boolean)
            .map((word) => word[0])
            .join('')
            .slice(0, 3)
            .toUpperCase();
    }

    getSkillLabel(skill) {
        if (typeof skill === 'string') {
            return skill;
        }
        const nombre = skill?.nombre || 'Habilidad';
        const nivel = skill?.nivel ? ` (${skill.nivel})` : '';
        return `${nombre}${nivel}`;
    }

    translateStatus(status) {
        const map = {
            DISPONIBLE: 'Disponible',
            OCUPADO: 'Ocupado',
            INACTIVO: 'Inactivo',
        };
        return map[status] || 'Sin estado';
    }

    showToast(message, type = 'info') {
        const toast = document.createElement('div');
        toast.textContent = message;
        toast.style.cssText = `
            position: fixed;
            bottom: 24px;
            right: 24px;
            background: ${type === 'success' ? '#12B76A' : type === 'error' ? '#F04438' : '#363F72'};
            color: #fff;
            padding: 12px 20px;
            border-radius: 8px;
            box-shadow: 0 10px 20px rgba(0,0,0,0.15);
            z-index: 11000;
            font-size: 14px;
            animation: slideIn 0.3s ease-out;
        `;
        document.body.appendChild(toast);
        setTimeout(() => {
            toast.style.animation = 'slideOut 0.3s ease-in';
            setTimeout(() => toast.remove(), 300);
        }, 3700);
    }
}

window.addEventListener('DOMContentLoaded', () => {
    window.profileManager = new ProfileManager();
});