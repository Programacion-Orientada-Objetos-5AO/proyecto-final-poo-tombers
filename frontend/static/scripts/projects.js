// Archivo: scripts/projects.js
// Obtiene proyectos desde el backend y actualiza las vistas del panel principal.

// Utilidad para construir URLs publicas de recursos alojados en el backend.
const resolveAssetUrl = (path) => {
    if (!path) {
        return null;
    }
    if (/^https?:\/\//i.test(path)) {
        return path;
    }
    const base = window.apiClient?.baseUrl ?? '';
    const normalizedBase = base.endsWith('/') ? base.slice(0, -1) : base;
    const normalizedPath = path.startsWith('/') ? path : `/${path}`;
    return `${normalizedBase}${normalizedPath}`;
};

window.resolveAssetUrl = window.resolveAssetUrl || resolveAssetUrl;

class ProjectsManager {
    constructor() {
        this.projects = [];
        this.allProjects = [];
        this.currentProjectIndex = 0;
        this.viewedProjects = new Set();
        this.cardContainer = document.querySelector('.card-container');
        this.projectCard = document.getElementById('project-card');
        this.expandedCard = document.getElementById('expanded-card');
        this.searchInput = document.getElementById('search-projects');
        this.joinButton = null;
        this.rotateIcon = null;
        this.eventHandlersAttached = false;
        this.init();
    }

    /**
     * Inicializa el módulo validando la sesión y cargando la información remota.
     */
    async init() {
        if (!window.apiClient || !window.apiClient.auth.isAuthenticated()) {
            window.location.href = '/';
            return;
        }

        await this.loadProjects();
        this.renderCurrentProject();
        this.attachGlobalHandlers();
    }

    /**
     * Recupera la lista de proyectos desde el backend.
     */
    async loadProjects() {
        try {
            const response = await window.apiClient.get('/api/projects');
            const projects = Array.isArray(response) ? response : [];
            this.projects = projects.map((project) => this.normalizeProject(project)).filter(Boolean);
            this.allProjects = [...this.projects];
        } catch (error) {
            console.error('Error al cargar los proyectos:', error);
            this.showNoProjectsMessage('No se pudieron cargar los proyectos. Intentá nuevamente más tarde.');
        }
    }

    /**
     * Asegura que cada proyecto contenga la estructura que la interfaz espera.
     */
    normalizeProject(project) {
        if (!project) {
            return null;
        }

        const rawProgress = project.progress ?? project.stats?.progress ?? project.progressPercentage;
        let fallbackProgress = rawProgress;
        if (fallbackProgress == null && typeof project.teamCurrent === 'number' && typeof project.teamMax === 'number' && project.teamMax > 0) {
            fallbackProgress = (project.teamCurrent / project.teamMax) * 100;
        }
        const progress = this.clampProgress(fallbackProgress);
        const stats = {
            teamCurrent: project.teamCurrent ?? 0,
            teamMax: project.teamMax ?? 0,
            duration: project.duration || 'Por definir',
            language: project.language || 'Sin idioma',
            type: project.type || 'General',
            progress,
        };

        const technologies = (Array.isArray(project.technologies) ? project.technologies : [])
            .map((tech) => {
                if (typeof tech === 'string') {
                    const nombre = tech.trim();
                    return nombre ? { nombre, nivel: '' } : null;
                }
                const nombre = typeof tech?.nombre === 'string' ? tech.nombre.trim() : null;
                const nivel = typeof tech?.nivel === 'string' ? tech.nivel.trim() : '';
                return nombre ? { nombre, nivel } : null;
            })
            .filter(Boolean);

        const objectives = (Array.isArray(project.objectives) ? project.objectives : [])
            .map((objective) => {
                if (typeof objective === 'string') {
                    return objective.trim();
                }
                return typeof objective?.text === 'string' ? objective.text.trim() : null;
            })
            .filter(Boolean);

        const skillsNeeded = (Array.isArray(project.skillsNeeded) ? project.skillsNeeded : [])
            .filter((skill) => Boolean(skill))
            .map((skill) => ({
                nombre: typeof skill?.nombre === 'string' ? skill.nombre.trim() : null,
                nivel: typeof skill?.nivel === 'string' ? skill.nivel.trim() : 'Intermedio',
            }))
            .filter((skill) => Boolean(skill.nombre));

        const statusValue = typeof project.status === 'string'
            ? project.status.toUpperCase()
            : typeof project.status?.name === 'string'
                ? project.status.name.toUpperCase()
                : 'ACTIVE';

        return {
            id: project.id,
            title: project.title || 'Proyecto sin título',
            description: project.description || 'Sin descripción disponible.',
            bannerUrl: resolveAssetUrl(project.bannerUrl) || '/static/imagenes/coding-foto-ejemplo.jpg',
            stats,
            technologies,
            objectives,
            skillsNeeded,
            status: statusValue,
            repositoryUrl: project.repositoryUrl || null,
            contactEmail: project.contactEmail || null,
            progress,
        };
    }

    /**
     * Renderiza el proyecto actual o muestra un mensaje si no hay datos.
     */
    renderCurrentProject() {
        if (this.projects.length === 0) {
            this.showNoProjectsMessage('Todavía no hay proyectos publicados. ¡Creá el primero!');
            return;
        }

        const nextProject = this.getNextUnviewedProject();
        if (!nextProject) {
            this.showAllProjectsViewedMessage();
            return;
        }

        this.currentProjectIndex = this.projects.findIndex((project) => project.id === nextProject.id);
        this.updateProjectCard(nextProject);
        this.updateExpandedCard(nextProject);
        this.markProjectAsViewed(nextProject.id);
        this.ensureHandlers();
    }

    /**
     * Actualiza la tarjeta principal del carrusel.
     */
    updateProjectCard(project) {
        const title = this.projectCard?.querySelector('.card-title');
        const description = this.projectCard?.querySelector('.card-description');
        const image = this.projectCard?.querySelector('.card-image');
        const statsText = this.projectCard?.querySelectorAll('.stats-text');
        const techContainer = this.projectCard?.querySelector('.card-tech');

        if (title) {
            title.textContent = project.title;
        }
        if (description) {
            description.textContent = project.description;
        }
        if (image) {
            const bannerSrc = project.bannerUrl || '/static/imagenes/coding-foto-ejemplo.jpg';
            image.style.backgroundImage = `url('${bannerSrc}')`;
        }

        if (statsText?.length >= 4) {
            statsText[0].textContent = `${project.stats.teamCurrent}/${project.stats.teamMax}`;
            statsText[1].textContent = project.stats.duration;
            statsText[2].textContent = project.stats.language;
            statsText[3].textContent = project.stats.type;
        }

        if (techContainer) {
            techContainer.innerHTML = '';
            const technologies = project.technologies.length > 0 ? project.technologies : ['Tecnologías a definir'];
            technologies.slice(0, 4).forEach((tech) => {
                const badge = document.createElement('div');
                badge.className = 'tech-icon';
                badge.textContent = this.getInitials(typeof tech === 'string' ? tech : tech?.nombre);
                techContainer.appendChild(badge);
            });
        }
    }

    /**
     * Actualiza la tarjeta expandida con toda la información disponible.
     */
    updateExpandedCard(project) {
        if (!this.expandedCard) {
            return;
        }

        const expandedTitle = this.expandedCard.querySelector('.expanded-title');
        if (expandedTitle) {
            expandedTitle.textContent = project.title;
        }

        const statusBadge = this.expandedCard.querySelector('.status-badge');
        if (statusBadge) {
            const statusKey = (project.status || 'ACTIVE').toUpperCase();
            statusBadge.textContent = this.translateStatus(statusKey);
            const statusClassMap = {
                ACTIVE: 'active',
                INACTIVE: 'inactive',
                COMPLETED: 'completed',
                ON_HOLD: 'on-hold',
            };
            const statusClass = statusClassMap[statusKey] || '';
            statusBadge.className = `status-badge ${statusClass}`.trim();
        }

        const expandedImage = this.expandedCard.querySelector('.expanded-image img');
        if (expandedImage) {
            expandedImage.src = project.bannerUrl || '/static/imagenes/coding-foto-ejemplo.jpg';
        }

        const stats = project.stats || {
            teamCurrent: project.teamCurrent ?? 0,
            teamMax: project.teamMax ?? 0,
            duration: project.duration || 'Por definir',
            language: project.language || 'Sin idioma',
            type: project.type || 'General',
            progress: project.progress ?? 0,
        };

        const statValues = this.expandedCard.querySelectorAll('.stat-value');
        if (statValues.length >= 4) {
            const teamValue = `${stats.teamCurrent ?? 0}/${stats.teamMax ?? '??'}`;
            statValues[0].textContent = teamValue;
            statValues[1].textContent = stats.duration || 'Por definir';
            statValues[2].textContent = stats.language || 'Sin idioma';
            statValues[3].textContent = stats.type || 'General';
        }

        const progressValue = this.clampProgress(project.progress ?? stats.progress);
        project.progress = progressValue;
        stats.progress = progressValue;

        const progressFill = this.expandedCard.querySelector('.progress-fill');
        if (progressFill) {
            progressFill.style.width = `${progressValue}%`;
        }
        const progressText = this.expandedCard.querySelector('.progress-text');
        if (progressText) {
            progressText.textContent = `${progressValue}% completado`;
        }

        const descriptionSection = this.expandedCard.querySelector('.section-content');
        if (descriptionSection) {
            descriptionSection.textContent = project.description;
        }

        this.renderList(
            this.expandedCard.querySelector('.tech-grid'),
            project.technologies,
            (tech) => this.buildTechItem(tech),
        );

        this.renderList(
            this.expandedCard.querySelector('.objectives-list'),
            project.objectives,
            (objective) => this.buildObjectiveItem(objective),
        );

        this.renderList(
            this.expandedCard.querySelector('.skills-grid'),
            project.skillsNeeded,
            (skill) => this.buildSkillBadge(skill),
        );
    }

    /**
     * Permite reutilizar la lógica de renderizado de listas.
     */
    renderList(container, items, builder) {
        if (!container) {
            return;
        }

        container.innerHTML = '';
        const normalizedItems = Array.isArray(items) && items.length > 0 ? items : ['Sin información disponible'];

        normalizedItems.forEach((item) => {
            const element = builder(item);
            if (element) {
                container.appendChild(element);
            }
        });
    }

    buildTechItem(tech) {
        const wrapper = document.createElement('div');
        wrapper.className = 'tech-item';

        const icon = document.createElement('span');
        icon.className = 'tech-icon-expanded';
        icon.textContent = this.getInitials(typeof tech === 'string' ? tech : tech?.nombre);

        const details = document.createElement('div');
        details.className = 'tech-details';

        const name = document.createElement('span');
        name.className = 'tech-name';
        name.textContent = typeof tech === 'string' ? tech : tech?.nombre || 'Tecnología';

        const level = document.createElement('span');
        level.className = 'tech-level';
        const nivel = typeof tech === 'object' && tech?.nivel ? tech.nivel : '';
        level.textContent = nivel;

        details.appendChild(name);
        if (nivel) {
            details.appendChild(level);
        }

        wrapper.appendChild(icon);
        wrapper.appendChild(details);
        return wrapper;
    }

    buildObjectiveItem(objective) {
        const item = document.createElement('div');
        item.className = 'objective-item';
        item.textContent = typeof objective === 'string' ? objective : objective?.text || 'Objetivo pendiente';
        return item;
    }

    buildSkillBadge(skill) {
        const badge = document.createElement('div');
        badge.className = 'skill-badge';
        if (typeof skill === 'string') {
            badge.textContent = skill;
        } else if (skill && typeof skill === 'object') {
            const nombre = skill.nombre || 'Habilidad';
            const nivel = skill.nivel ? ` (${skill.nivel})` : '';
            badge.textContent = `${nombre}${nivel}`;
        } else {
            badge.textContent = 'Habilidad por definir';
        }
        return badge;
    }

    /**
     * Devuelve las iniciales de una cadena para las etiquetas circulares.
     */
    getInitials(text) {
        if (!text) {
            return 'N/A';
        }

        return text
            .split(/\s+/)
            .filter(Boolean)
            .map((word) => word[0])
            .join('')
            .slice(0, 3)
            .toUpperCase();
    }

    /**
     * Normaliza el porcentaje de avance recibido.
     */
    clampProgress(value) {
        const numeric = Number(value);
        if (!Number.isFinite(numeric)) {
            return 0;
        }
        return Math.min(100, Math.max(0, Math.round(numeric)));
    }

    translateStatus(status) {
        const map = {
            ACTIVE: 'Activo',
            INACTIVE: 'En pausa',
            COMPLETED: 'Completado',
            ON_HOLD: 'En planificación',
        };
        return map[status] || 'Activo';
    }

    /**
     * Gestiona los listeners que dependen de los nodos renderizados.
     */
    ensureHandlers() {
        if (this.eventHandlersAttached) {
            return;
        }

        this.rotateIcon = document.querySelector('.rotate-icon');
        this.joinButton = document.querySelector('.action-btn.primary');

        this.rotateIcon?.addEventListener('click', () => this.nextProject());
        this.joinButton?.addEventListener('click', async () => {
            const project = this.getCurrentProject();
            if (!project) {
                return;
            }
            // Reutilizamos la misma lógica que el swipe hacia la derecha.
            await this.handleCardSwipe('right');
            const message = `Te uniste al proyecto "${project.title}"`;
            if (typeof window.showToastMessage === 'function') {
                window.showToastMessage(message, 'exito');
            } else {
                alert(message);
            }
        });

        const contactButton = document.querySelector('.action-btn.tertiary');
        contactButton?.addEventListener('click', () => {
            const project = this.getCurrentProject();
            if (!project || !project.contactEmail) {
                alert('El equipo aún no publicó un contacto.');
                return;
            }
            window.location.href = `mailto:${project.contactEmail}`;
        });

        this.eventHandlersAttached = true;
    }

    attachGlobalHandlers() {
        if (this.searchInput) {
            this.searchInput.addEventListener('input', (event) => {
                const query = event.target.value;
                this.searchProjects(query);
            });
        }
    }

    /**
     * Marca un proyecto como visualizado para evitar repeticiones.
     */
    markProjectAsViewed(projectId) {
        if (projectId) {
            this.viewedProjects.add(projectId);
        }
    }

    getNextUnviewedProject() {
        return this.projects.find((project) => !this.viewedProjects.has(project.id)) || null;
    }

    getCurrentProject() {
        return this.projects[this.currentProjectIndex] || null;
    }

    nextProject() {
        if (this.projects.length === 0) {
            return;
        }

        this.currentProjectIndex = (this.currentProjectIndex + 1) % this.projects.length;
        const project = this.getCurrentProject();
        this.updateProjectCard(project);
        this.updateExpandedCard(project);
        this.markProjectAsViewed(project.id);
    }

    previousProject() {
        if (this.projects.length === 0) {
            return;
        }

        this.currentProjectIndex = (this.currentProjectIndex - 1 + this.projects.length) % this.projects.length;
        const project = this.getCurrentProject();
        this.updateProjectCard(project);
        this.updateExpandedCard(project);
    }

    restartProjects() {
        this.viewedProjects.clear();
        this.currentProjectIndex = 0;
        this.renderCurrentProject();
    }

    /**
     * Maneja los gestos de swipe que provienen de script.js.
     */
    async handleCardSwipe(direction) {
        const project = this.getCurrentProject();
        if (!project) {
            return;
        }

        if (direction === 'right') {
            await this.registerLike(project.id);
            this.nextProject();
        } else if (direction === 'left') {
            await this.registerDislike(project.id);
            this.nextProject();
        } else if (direction === 'up') {
            this.updateExpandedCard(project);
            this.expandedCard?.classList.remove('hidden');
            this.expandedCard?.classList.add('visible');
        }
    }

    async registerLike(projectId) {
        if (!projectId) {
            return;
        }

        try {
            await window.apiClient.post(`/api/projects/${projectId}/like`);
        } catch (error) {
            console.error('No se pudo registrar el like:', error);
        }
    }

    async registerDislike(projectId) {
        if (!projectId) {
            return;
        }

        try {
            await window.apiClient.post(`/api/projects/${projectId}/dislike`);
        } catch (error) {
            console.error('No se pudo registrar el dislike:', error);
        }
    }

    /**
     * Permite filtrar proyectos por título, tecnologías o habilidades.
     */
    searchProjects(query) {
        const normalized = (query || '').trim().toLowerCase();

        if (!normalized) {
            this.projects = [...this.allProjects];
        } else {
            this.projects = this.allProjects.filter((project) => {
                const titleMatch = project.title.toLowerCase().includes(normalized);
                const techMatch = project.technologies.some((tech) => {
                    const value = typeof tech === 'string' ? tech : tech?.nombre || '';
                    return value.toLowerCase().includes(normalized);
                });
                const skillMatch = project.skillsNeeded.some((skill) => {
                    if (typeof skill === 'string') {
                        return skill.toLowerCase().includes(normalized);
                    }
                    const value = `${skill?.nombre || ''} ${skill?.nivel || ''}`;
                    return value.trim().toLowerCase().includes(normalized);
                });
                return titleMatch || techMatch || skillMatch;
            });
        }

        this.viewedProjects.clear();
        this.currentProjectIndex = 0;
        this.renderCurrentProject();
    }

    showNoProjectsMessage(message) {
        if (!this.cardContainer) {
            return;
        }

        this.cardContainer.innerHTML = `
            <div class="empty-state">
                <h2>${message}</h2>
                <p>Podés crear un proyecto nuevo con el botón “Crear proyecto”.</p>
            </div>
        `;
        const indicators = document.getElementById('status-indicators');
        if (indicators) {
            indicators.style.display = 'none';
        }
    }

    showAllProjectsViewedMessage() {
        if (!this.cardContainer) {
            return;
        }

        this.cardContainer.innerHTML = `
            <div class="all-projects-viewed-message">
                <div class="all-projects-icon">🎉</div>
                <h2>¡Ya revisaste todos los proyectos!</h2>
                <p>Reiniciá el listado para volver a examinarlos o esperá nuevos proyectos.</p>
                <button class="restart-button" onclick="window.projectsManager.restartProjects()">Verlos nuevamente</button>
            </div>
        `;
    }

    getViewingStats() {
        const total = this.projects.length;
        const viewed = this.viewedProjects.size;
        const remaining = Math.max(total - viewed, 0);
        const percentage = total > 0 ? Math.round((viewed / total) * 100) : 0;
        return { total, viewed, remaining, percentage };
    }

    /**
     * Agrega un proyecto recién creado sin recargar toda la lista.
     */
    addProject(projectResponse) {
        const normalized = this.normalizeProject(projectResponse);
        if (!normalized) {
            return;
        }

        this.allProjects.unshift(normalized);
        this.projects = [...this.allProjects];
        this.viewedProjects.clear();
        this.currentProjectIndex = 0;
        this.renderCurrentProject();
    }
}

// Instancia global para que otros scripts puedan interactuar.
document.addEventListener('DOMContentLoaded', () => {
    window.projectsManager = new ProjectsManager();
});

// Funciones auxiliares utilizadas por otras partes del frontend.
function loadNextProject() {
    window.projectsManager?.nextProject();
}

function loadPreviousProject() {
    window.projectsManager?.previousProject();
}

function refreshProjectList() {
    window.projectsManager?.restartProjects();
}

function handleSwipe(direction) {
    window.projectsManager?.handleCardSwipe(direction);
}

function getViewingStats() {
    return window.projectsManager?.getViewingStats() ?? null;
}


