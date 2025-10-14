// Vista de interesados estilo "Tinder" para administradores de proyecto.

(() => {
    const TOAST = (message, type = 'info') => {
        if (typeof showToastMessage === 'function') {
            showToastMessage(message, type);
        } else {
            console.log(`[${type}] ${message}`);
        }
    };

    const getFullName = (user) => {
        const first = user?.firstName?.trim() || '';
        const last = user?.lastName?.trim() || '';
        const fullName = `${first} ${last}`.trim();
        if (fullName) {
            return fullName;
        }
        if (user?.username) {
            return user.username;
        }
        return user?.email || 'Usuario sin nombre';
    };

    const formatLanguages = (languages) => {
        if (!languages) {
            return 'Sin datos';
        }
        return languages;
    };

    const formatStatusBadge = (statusRaw) => {
        const status = (statusRaw || '').toUpperCase();
        switch (status) {
            case 'DISPONIBLE':
                return { label: 'Disponible', className: 'active' };
            case 'OCUPADO':
                return { label: 'Ocupado', className: 'on-hold' };
            case 'INACTIVO':
                return { label: 'Inactivo', className: 'inactive' };
            default:
                return { label: status || 'Activo', className: 'active' };
        }
    };

    const buildSkillBadge = (skill) => {
        const wrapper = document.createElement('div');
        wrapper.className = 'skill-badge';
        if (!skill) {
            wrapper.textContent = 'Habilidad sin nombre';
            return wrapper;
        }
        if (typeof skill === 'string') {
            wrapper.textContent = skill;
            return wrapper;
        }
        const nombre = skill.nombre || 'Habilidad';
        const nivel = skill.nivel ? ` (${skill.nivel})` : '';
        wrapper.textContent = `${nombre}${nivel}`;
        return wrapper;
    };

    class InterestedDeck {
        constructor() {
            this.projectId = Number(document.body.dataset.projectId);
            if (!this.projectId) {
                TOAST('No se pudo identificar el proyecto.', 'error');
                return;
            }

            this.projectTitle = document.body.dataset.projectTitle || '';
            this.card = document.getElementById('project-card');
            this.cardContainer = document.querySelector('.card-container');
            this.cardTitle = this.card?.querySelector('.card-title');
            this.cardImage = this.card?.querySelector('.card-image');
            this.statsText = this.card?.querySelectorAll('.stats-text');
            this.cardDescription = this.card?.querySelector('.card-description');
            this.cardTech = this.card?.querySelector('.card-tech');
            this.titleElement = document.querySelector('.view-intro h1');
            this.subtitleElement = document.querySelector('.view-intro p');

            this.acceptButton = document.getElementById('accept-user-button');
            this.rejectButton = document.getElementById('reject-user-button');
            this.viewButton = document.getElementById('view-user-button');

            this.statusIndicators = document.getElementById('status-indicators');
            this.indicatorRight = this.statusIndicators?.querySelector('.like-indicator');
            this.indicatorLeft = this.statusIndicators?.querySelector('.dislike-indicator');
            this.indicatorUp = this.statusIndicators?.querySelector('.ampliar-indicator');

            this.expandedCard = document.getElementById('expanded-card');
            this.closeExpandedBtn = document.getElementById('close-expanded');
            this.expandedAcceptBtn = document.getElementById('expanded-accept-button');
            this.expandedRejectBtn = document.getElementById('expanded-reject-button');
            this.expandedTitle = this.expandedCard?.querySelector('.expanded-title');
            this.expandedStatus = document.getElementById('expanded-status');
            this.expandedAvatar = document.getElementById('expanded-avatar');
            this.expandedAge = document.getElementById('expanded-age');
            this.expandedRole = document.getElementById('expanded-role');
            this.expandedLanguages = document.getElementById('expanded-languages');
            this.expandedInterestsCount = document.getElementById('expanded-interests-count');
            this.expandedBio = document.getElementById('expanded-bio');
            this.expandedSkills = document.getElementById('expanded-skills');
            this.expandedInterests = document.getElementById('expanded-interests');
            this.expandedLinks = document.getElementById('expanded-links');

            this.users = [];
            this.currentIndex = 0;
            this.totalInterested = 0;
            this.acceptedCount = 0;
            this.summaryCallback = null;
            this.isDragging = false;
            this.expandedOpen = false;
            this.isBusy = false;
            this.startX = 0;
            this.startY = 0;
            this.offsetX = 0;
            this.offsetY = 0;

            this.ensureAuth();
            this.attachHandlers();
            this.loadUsers();
        }

        ensureAuth() {
            if (!window.apiClient?.auth?.isAuthenticated()) {
                window.location.href = '/';
            }
        }

        setSummaryCallback(callback) {
            this.summaryCallback = typeof callback === 'function' ? callback : null;
            this.notifySummary();
        }

        notifySummary() {
            if (typeof this.summaryCallback !== 'function') {
                return;
            }
            const users = Array.isArray(this.users) ? this.users : [];
            const available = users.filter(
                (user) => (user.status || '').toUpperCase() === 'DISPONIBLE',
            ).length;
            const detail = {
                total: users.length,
                available,
                accepted: Number(this.acceptedCount || 0),
            };
            try {
                this.summaryCallback(detail);
            } catch (error) {
                console.error('Resumen de interesados', error);
            }
        }

        attachHandlers() {
            this.acceptButton?.addEventListener('click', () => this.animateDecision('ACCEPT'));
            this.rejectButton?.addEventListener('click', () => this.animateDecision('REJECT'));
            this.viewButton?.addEventListener('click', () => this.openExpanded());
            this.closeExpandedBtn?.addEventListener('click', () => this.closeExpanded());
            this.expandedAcceptBtn?.addEventListener('click', () => this.animateDecision('ACCEPT'));
            this.expandedRejectBtn?.addEventListener('click', () => this.animateDecision('REJECT'));

            if (this.card) {
                this.card.addEventListener('mousedown', (event) => this.startDrag(event));
                this.card.addEventListener('touchstart', (event) => this.startDrag(event));
                window.addEventListener('mousemove', (event) => this.moveDrag(event));
                window.addEventListener('touchmove', (event) => this.moveDrag(event), { passive: false });
                window.addEventListener('mouseup', () => this.endDrag());
                window.addEventListener('touchend', () => this.endDrag());
            }
        }

        async loadUsers() {
            if (!window.apiClient) {
                this.showEmptyState('No se puede acceder al servidor.');
                return;
            }

            try {
                const response = await window.apiClient.get(`/api/projects/${this.projectId}/interested`);
                const users = Array.isArray(response?.interestedUsers) ? response.interestedUsers : [];
                this.users = users;
                this.currentIndex = 0;
                this.totalInterested = Number(response?.totalInterested ?? users.length);
                await this.loadProjectInfo(response?.project);
                this.renderCurrentUser();
            } catch (error) {
                console.error('Error al cargar interesados:', error);
                this.showEmptyState('No se pudo cargar la lista de interesados. Intenta mas tarde.');
                TOAST('No se pudo cargar la lista de interesados.', 'error');
            }
        }

        async loadProjectInfo(projectFromResponse) {
            try {
                let projectData = projectFromResponse;
                let membersCount = null;

                const resolveMembers = (data) => {
                    if (!data) {
                        return null;
                    }
                    if (Array.isArray(data.members)) {
                        return data.members.length;
                    }
                    if (Array.isArray(data.acceptedUsers)) {
                        return data.acceptedUsers.length;
                    }
                    if (typeof data.accepted === 'number') {
                        return Number(data.accepted);
                    }
                    if (typeof data?.stats?.team_current !== 'undefined') {
                        return Number(data.stats.team_current);
                    }
                    return null;
                };

                membersCount = resolveMembers(projectData);
                if (membersCount === null) {
                    projectData = await window.apiClient.get(`/api/projects/${this.projectId}`);
                    membersCount = resolveMembers(projectData);
                }

                if (membersCount === null) {
                    membersCount = 0;
                }

                this.acceptedCount = Math.max(0, Number(membersCount) - 1);
            } catch (error) {
                console.warn('No se pudo obtener informacion del proyecto', error);
            } finally {
                this.notifySummary();
            }
        }

        getCurrentUser() {
            return this.users[this.currentIndex] || null;
        }

        renderCurrentUser() {
            if (this.users.length === 0) {
                this.showEmptyState('No hay personas interesadas en este momento.');
                return;
            }

            const user = this.getCurrentUser();
            if (!user) {
                this.showEmptyState('No hay mas postulaciones pendientes.');
                return;
            }

            if (this.titleElement) {
                const remaining = this.users.length;
                this.titleElement.textContent =
                    remaining === 1 ? '1 persona interesada' : `${remaining} personas interesadas`;
            }
            if (this.subtitleElement) {
                this.subtitleElement.innerHTML = `Gestiona quienes quieren sumarse a <strong>${this.projectTitle || 'tu proyecto'}</strong>. Desliza a la derecha para aceptarlos, a la izquierda para rechazarlos o hacia arriba para ver su perfil en detalle.`;
            }

            if (this.cardContainer && !this.cardContainer.contains(this.card)) {
                this.cardContainer.innerHTML = '';
                this.cardContainer.appendChild(this.card);
            }

            this.card.style.opacity = '1';
            this.card.style.transform = 'translate(0, 0) rotate(0deg)';
            const name = getFullName(user);
            if (this.cardTitle) {
                this.cardTitle.textContent = name;
            }

            if (this.cardImage) {
                const avatar = user.profilePictureUrl || '/static/imagenes/profile-placeholder.svg';
                this.cardImage.style.backgroundImage = `url('${avatar}')`;
            }

            const specialization = user.specialization || user.role || 'Sin especialidad';
            const availability = formatStatusBadge(user.status).label;
            const languages = formatLanguages(user.languages);
            const summary = user.summary || user.bio || 'La persona todavia no cargo una presentacion personal.';

            if (this.statsText?.length >= 4) {
                this.statsText[0].textContent = specialization;
                this.statsText[1].textContent = availability;
                this.statsText[2].textContent = languages;
                this.statsText[3].textContent = user.interests?.length
                    ? `${user.interests.length} intereses`
                    : 'Sin intereses';
            }

            if (this.cardDescription) {
                this.cardDescription.textContent = summary;
            }

            if (this.cardTech) {
                this.cardTech.innerHTML = '';
                const skills = Array.isArray(user.skills) ? user.skills : [];
                if (skills.length === 0) {
                    const placeholder = document.createElement('div');
                    placeholder.className = 'tech-icon';
                    placeholder.textContent = 'Sin datos';
                    this.cardTech.appendChild(placeholder);
                } else {
                    skills.slice(0, 4).forEach((skill) => {
                        const chip = document.createElement('div');
                        chip.className = 'tech-icon';
                        if (typeof skill === 'string') {
                            chip.textContent = skill.slice(0, 6);
                        } else {
                            const nombre = skill?.nombre || 'Skill';
                            chip.textContent = nombre.slice(0, 6);
                        }
                        this.cardTech.appendChild(chip);
                    });
                }
            }

            this.notifySummary();
            this.setButtonsEnabled(true);
            if (this.statusIndicators) {
                this.statusIndicators.style.display = 'flex';
                this.statusIndicators.style.opacity = '0';
            }
        }

        setButtonsEnabled(enabled) {
            [this.acceptButton, this.rejectButton, this.viewButton, this.expandedAcceptBtn, this.expandedRejectBtn].forEach(
                (button) => {
                    if (button) {
                        button.disabled = !enabled;
                    }
                },
            );
        }

        showEmptyState(message) {
            this.setButtonsEnabled(false);
            if (this.statusIndicators) {
                this.statusIndicators.style.display = 'none';
            }
            if (this.titleElement) {
                this.titleElement.textContent = 'Personas interesadas';
            }
            if (this.subtitleElement) {
                this.subtitleElement.innerHTML = `Por ahora no hay postulaciones para <strong>${this.projectTitle || 'tu proyecto'}</strong>. Apenas alguien se interese la vas a ver en esta vista.`;
            }
            if (this.cardContainer) {
                this.cardContainer.innerHTML = `
                    <div class="empty-state">
                        <h2>${message}</h2>
                        <p>Cuando alguien se interese por tu proyecto se mostrara aca para que puedas aceptarlo.</p>
                    </div>
                `;
            }
            this.users = [];
            this.currentIndex = 0;
            this.totalInterested = 0;
            this.notifySummary();
        }

        startDrag(event) {
            if (!this.card || this.expandedOpen || this.isBusy || !this.getCurrentUser()) {
                return;
            }
            this.isDragging = true;
            this.offsetX = 0;
            this.offsetY = 0;
            this.card.style.transition = 'none';

            if (event.type === 'touchstart') {
                this.startX = event.touches[0].clientX;
                this.startY = event.touches[0].clientY;
            } else {
                this.startX = event.clientX;
                this.startY = event.clientY;
            }
        }

        moveDrag(event) {
            if (!this.isDragging || !this.card || this.expandedOpen) {
                return;
            }

            let currentX;
            let currentY;

            if (event.type === 'touchmove') {
                event.preventDefault();
                currentX = event.touches[0].clientX;
                currentY = event.touches[0].clientY;
            } else {
                currentX = event.clientX;
                currentY = event.clientY;
            }

            this.offsetX = currentX - this.startX;
            this.offsetY = currentY - this.startY;

            const rotate = this.offsetX * 0.1;
            this.card.style.transform = `translate(${this.offsetX}px, ${this.offsetY}px) rotate(${rotate}deg)`;
            this.updateIndicators();
        }

        updateIndicators() {
            if (!this.statusIndicators) {
                return;
            }
            this.statusIndicators.style.opacity = '1';
            const rightActive = this.offsetX > 60;
            const leftActive = this.offsetX < -60;
            const upActive = this.offsetY < -60;

            if (this.indicatorRight) {
                this.indicatorRight.style.opacity = rightActive ? '1' : '0';
            }
            if (this.indicatorLeft) {
                this.indicatorLeft.style.opacity = leftActive ? '1' : '0';
            }
            if (this.indicatorUp) {
                this.indicatorUp.style.opacity = upActive ? '1' : '0';
            }
        }

        endDrag() {
            if (!this.isDragging || !this.card) {
                return;
            }
            this.isDragging = false;

            const horizontalThreshold = 120;
            const verticalThreshold = 150;

            if (this.offsetX > horizontalThreshold) {
                this.animateSwipe('right');
            } else if (this.offsetX < -horizontalThreshold) {
                this.animateSwipe('left');
            } else if (this.offsetY < -verticalThreshold) {
                this.animateSwipe('up');
            } else {
                this.resetCardPosition();
            }

            if (this.statusIndicators) {
                this.statusIndicators.style.opacity = '0';
            }
        }

        animateSwipe(direction) {
            if (!this.card || this.isBusy) {
                if (direction === 'up') {
                    this.openExpanded();
                }
                return;
            }

            if (direction === 'up') {
                this.card.style.transition = 'transform 0.4s ease, opacity 0.4s ease';
                this.card.style.transform = `translate(0, -${window.innerHeight}px) rotate(0deg)`;
                this.card.style.opacity = '0';
                setTimeout(() => {
                    this.resetCardPosition(false);
                    this.openExpanded();
                }, 320);
                return;
            }

            const horizontalDistance = direction === 'right' ? window.innerWidth : -window.innerWidth;
            const rotation = direction === 'right' ? 25 : -25;
            const verticalOffset = Math.max(Math.min(this.offsetY, 200), -200);

            this.card.style.transition = 'transform 0.4s ease, opacity 0.4s ease';
            this.card.style.transform = `translate(${horizontalDistance}px, ${verticalOffset}px) rotate(${rotation}deg)`;
            this.card.style.opacity = '0';
            setTimeout(() => this.finalizeDecision(direction), 340);
        }

        animateDecision(action) {
            if (this.isBusy) {
                return;
            }
            if (this.expandedOpen) {
                this.closeExpanded();
            }
            if (action === 'ACCEPT') {
                this.animateSwipe('right');
            } else if (action === 'REJECT') {
                this.animateSwipe('left');
            } else if (action === 'VIEW') {
                this.openExpanded();
            }
        }

        resetCardPosition(animate = true) {
            if (!this.card) {
                return;
            }
            this.card.style.transition = animate ? 'transform 0.3s ease, opacity 0.3s ease' : 'none';
            this.card.style.transform = 'translate(0, 0) rotate(0deg)';
            this.card.style.opacity = '1';
        }

        async finalizeDecision(direction) {
            const user = this.getCurrentUser();
            if (!user) {
                this.resetCardPosition(false);
                return;
            }

            if (direction === 'right') {
                await this.handleDecision(user, 'ACCEPT');
            } else if (direction === 'left') {
                await this.handleDecision(user, 'REJECT');
            }
            this.resetCardPosition(false);
        }

        async handleDecision(user, action) {
            if (!user?.id) {
                return;
            }
            this.isBusy = true;
            this.setButtonsEnabled(false);

            try {
                await window.apiClient.post(`/api/projects/${this.projectId}/manage-interested`, {
                    userId: user.id,
                    action,
                });
                const message =
                    action === 'ACCEPT'
                        ? `${getFullName(user)} fue agregado a tu equipo.`
                        : `${getFullName(user)} fue rechazado.`;
                TOAST(message, 'exito');
                await this.loadProjectInfo();
                this.removeCurrentUser();
            } catch (error) {
                const message =
                    error?.data?.message || error?.data?.detail || error?.message || 'Operacion no disponible.';
                TOAST(message, 'error');
            } finally {
                this.isBusy = false;
                this.setButtonsEnabled(true);
            }
        }

        removeCurrentUser() {
            if (this.users.length === 0) {
                return;
            }
            this.users.splice(this.currentIndex, 1);
            this.totalInterested = Math.max(0, this.totalInterested - 1);
            if (this.currentIndex >= this.users.length) {
                this.currentIndex = 0;
            }
            if (this.users.length === 0) {
                this.showEmptyState('No quedan postulaciones pendientes.');
            } else {
                this.renderCurrentUser();
            }
        }

        openExpanded() {
            const user = this.getCurrentUser();
            if (!user || !this.expandedCard) {
                return;
            }
            this.expandedOpen = true;
            this.populateExpanded(user);
            this.expandedCard.classList.remove('hidden');
            this.expandedCard.classList.add('visible');
        }

        closeExpanded() {
            if (!this.expandedCard) {
                return;
            }
            this.expandedOpen = false;
            this.expandedCard.classList.remove('visible');
            setTimeout(() => {
                this.expandedCard?.classList.add('hidden');
            }, 220);
        }

        populateExpanded(user) {
            if (this.expandedTitle) {
                this.expandedTitle.textContent = getFullName(user);
            }
            if (this.expandedAvatar) {
                this.expandedAvatar.src = user.profilePictureUrl || '/static/imagenes/profile-placeholder.svg';
            }
            const statusInfo = formatStatusBadge(user.status);
            if (this.expandedStatus) {
                this.expandedStatus.textContent = statusInfo.label;
                this.expandedStatus.className = `status-badge ${statusInfo.className}`.trim();
            }
            if (this.expandedAge) {
                this.expandedAge.textContent = user.age ? `${user.age} años` : 'Sin datos';
            }
            if (this.expandedRole) {
                this.expandedRole.textContent = user.specialization || user.role || 'Sin definir';
            }
            if (this.expandedLanguages) {
                this.expandedLanguages.textContent = user.languages || 'Sin datos';
            }
            if (this.expandedInterestsCount) {
                const count = Array.isArray(user.interests) ? user.interests.length : 0;
                this.expandedInterestsCount.textContent = count ? `${count} items` : 'Sin datos';
            }

            if (this.expandedBio) {
                this.expandedBio.textContent =
                    user.bio ||
                    user.summary ||
                    'La persona todavia no cargo una presentacion personal. Aprovecha el chat para conocerla mejor.';
            }

            if (this.expandedSkills) {
                this.expandedSkills.innerHTML = '';
                const skills = Array.isArray(user.skills) ? user.skills : [];
                if (skills.length === 0) {
                    const placeholder = document.createElement('div');
                    placeholder.className = 'skill-badge';
                    placeholder.textContent = 'Sin habilidades listadas';
                    this.expandedSkills.appendChild(placeholder);
                } else {
                    skills.forEach((skill) => this.expandedSkills.appendChild(buildSkillBadge(skill)));
                }
            }

            if (this.expandedInterests) {
                this.expandedInterests.innerHTML = '';
                const interests = Array.isArray(user.interests) ? user.interests : [];
                if (interests.length === 0) {
                    const item = document.createElement('div');
                    item.className = 'objective-item';
                    item.textContent = 'Sin intereses cargados.';
                    this.expandedInterests.appendChild(item);
                } else {
                    interests.forEach((interest) => {
                        const item = document.createElement('div');
                        item.className = 'objective-item';
                        item.textContent = interest;
                        this.expandedInterests.appendChild(item);
                    });
                }
            }

            if (this.expandedLinks) {
                this.expandedLinks.innerHTML = '';
                const links = [];
                if (user.email) {
                    links.push({ label: 'Email', value: user.email, href: `mailto:${user.email}` });
                }
                if (user.phone) {
                    links.push({ label: 'Teléfono', value: user.phone, href: `tel:${user.phone}` });
                }
                if (user.linkedin) {
                    links.push({ label: 'LinkedIn', value: user.linkedin, href: user.linkedin });
                }
                if (user.github) {
                    links.push({ label: 'GitHub', value: user.github, href: user.github });
                }
                if (user.portfolio) {
                    links.push({ label: 'Portfolio', value: user.portfolio, href: user.portfolio });
                }

                if (links.length === 0) {
                    const empty = document.createElement('div');
                    empty.className = 'members-empty';
                    empty.textContent = 'Sin datos de contacto.';
                    this.expandedLinks.appendChild(empty);
                } else {
                    links.forEach((link) => {
                        const item = document.createElement('div');
                        item.className = 'member-item';

                        const info = document.createElement('div');
                        info.className = 'member-info';

                        const title = document.createElement('span');
                        title.className = 'member-name';
                        title.textContent = link.label;

                        const anchor = document.createElement(link.href ? 'a' : 'span');
                        anchor.className = 'member-email';
                        anchor.textContent = link.value;
                        if (link.href) {
                            anchor.href = link.href;
                            anchor.target = '_blank';
                            anchor.rel = 'noopener noreferrer';
                        }

                        info.appendChild(title);
                        info.appendChild(anchor);
                        item.appendChild(info);
                        this.expandedLinks.appendChild(item);
                    });
                }
            }
        }
    }

    document.addEventListener('DOMContentLoaded', () => {
        if (!window.apiClient?.auth?.isAuthenticated()) {
            window.location.href = '/';
            return;
        }
        window.interestedDeck = new InterestedDeck();
    });
})();
