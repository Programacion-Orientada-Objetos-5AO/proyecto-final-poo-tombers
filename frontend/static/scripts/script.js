// FEED - Versión mejorada con mejor gestión de eventos
document.addEventListener('DOMContentLoaded', () => {
    const expandedCard = document.getElementById('expanded-card');
    const closeExpanded = document.getElementById('close-expanded');
    let expandedView = false;

    let card = document.getElementById('project-card');
    const statusIndicators = document.getElementById('status-indicators');
    const likeIndicator = statusIndicators.querySelector('.like-indicator');
    const dislikeIndicator = statusIndicators.querySelector('.dislike-indicator');
    const ampliarIndicator = statusIndicators.querySelector('.ampliar-indicator');
    const sidebar = document.getElementById('sidebar');
    const sidebarTrigger = document.getElementById('sidebar-trigger');
    

    const createProjectBtn = document.getElementById('create-project-btn');
    const createProjectCard = document.getElementById('create-project-card');
    const closeCreate = document.getElementById('close-create');
    const cancelCreate = document.getElementById('cancel-create');
    const createForm = document.getElementById('create-form');


    const projectImageInput = document.getElementById('project-image');
    const imagePreview = document.getElementById('image-preview');
    const previewImg = document.getElementById('preview-img');
    const removeImageBtn = document.getElementById('remove-image');
    const imagePlaceholder = imagePreview.querySelector('.image-placeholder');
    
    let selectedImageFile = null;

    let startX = 0;
    let startY = 0;
    let offsetX = 0;
    let offsetY = 0;
    let isDragging = false;
    let isHoveringSidebar = false;


    projectImageInput?.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file && file.type.startsWith('image/')) {
            selectedImageFile = file;
            const reader = new FileReader();
            
            reader.onload = (e) => {
                previewImg.src = e.target.result;
                previewImg.style.display = 'block';
                removeImageBtn.style.display = 'block';
                imagePlaceholder.style.display = 'none';
            };
            
            reader.readAsDataURL(file);
        }
    });

    removeImageBtn?.addEventListener('click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        
        selectedImageFile = null;
        projectImageInput.value = '';
        previewImg.src = '';
        previewImg.style.display = 'none';
        removeImageBtn.style.display = 'none';
        imagePlaceholder.style.display = 'flex';
    });

    function closeCreateModal() {
        createProjectCard.classList.remove('visible');
        setTimeout(() => {
            createProjectCard.classList.add('hidden');
            createForm.reset();
            if (selectedImageFile) {
                selectedImageFile = null;
                previewImg.src = '';
                previewImg.style.display = 'none';
                removeImageBtn.style.display = 'none';
                imagePlaceholder.style.display = 'flex';
            }
        }, 400);
    }


    function closeCreateModal() {
        createProjectCard.classList.remove('visible');
        setTimeout(() => {
            createProjectCard.classList.add('hidden');
            createForm.reset();
        }, 400);
    }

    // Event listeners para crear proyecto
    closeCreate?.addEventListener('click', closeCreateModal);
    cancelCreate?.addEventListener('click', closeCreateModal);

    createProjectBtn?.addEventListener('click', () => {
        createProjectCard.classList.remove('hidden');
        setTimeout(() => {
            createProjectCard.classList.add('visible');
        }, 10);
    });

    createProjectCard?.addEventListener('click', (e) => {
        if (e.target === createProjectCard) {
            closeCreateModal();
        }
    });

    // Corregir el form submit (había código duplicado)
    createForm?.addEventListener('submit', (e) => {
        e.preventDefault();

        const formData = {
            name: document.getElementById('project-name').value,
            status: document.getElementById('project-status').value,
            teamSize: document.getElementById('team-size').value,
            duration: document.getElementById('duration').value,
            language: document.getElementById('language').value,
            type: document.getElementById('project-type').value,
            description: document.getElementById('description').value,
            technologies: document.getElementById('technologies').value.split(',').map(t => t.trim()),
            skills: document.getElementById('skills').value.split(',').map(s => s.trim()),
            image: selectedImageFile // Agregar la imagen al formData
        };

        console.log('Nuevo proyecto creado:', formData);
        alert('¡Proyecto creado exitosamente!');
        closeCreateModal();
    });
    
    // Theme toggles
    const toggle = document.getElementById('theme-toggle');
    const togglemo = document.getElementById('theme-togglemo');

    function syncThemeToggles(sourceToggle, targetToggle) {
        targetToggle.checked = sourceToggle.checked;
        document.body.classList.toggle('dark-mode', sourceToggle.checked);
    }

    toggle?.addEventListener('change', () => {
        syncThemeToggles(toggle, togglemo);
    });

    togglemo?.addEventListener('change', () => {
        syncThemeToggles(togglemo, toggle);
    });

    function setupSwipeHandlers(cardElement) {
        if (!cardElement) {
            console.warn('No se puede configurar swipe handlers: elemento no encontrado');
            return;
        }

        console.log('Configurando swipe handlers para:', cardElement.id);

        // Remover listeners existentes para evitar duplicados
        cardElement.removeEventListener('touchstart', handleStart);
        cardElement.removeEventListener('touchmove', handleMove);
        cardElement.removeEventListener('touchend', handleEnd);
        cardElement.removeEventListener('mousedown', handleStart);

        // Agregar nuevos listeners
        cardElement.addEventListener('touchstart', handleStart, { passive: false });
        cardElement.addEventListener('touchmove', handleMove, { passive: false });
        cardElement.addEventListener('touchend', handleEnd);
        cardElement.addEventListener('mousedown', handleStart);

        // Actualizar referencia global
        card = cardElement;
    }

    // Configurar handlers iniciales
    if (card) {
        setupSwipeHandlers(card);
    }

    // Agregar listeners globales para mouse (estos no se duplican)
    document.addEventListener('mousemove', handleMove);
    document.addEventListener('mouseup', handleEnd);

    // Sidebar trigger hover handler
    sidebarTrigger?.addEventListener('mouseenter', () => {
        if (!isDragging) {
            isHoveringSidebar = true;
            sidebar.classList.add('active');
        }
    });

    sidebar?.addEventListener('mouseleave', () => {
        isHoveringSidebar = false;
        sidebar.classList.remove('active');
    });

    document.addEventListener('mousemove', (e) => {
        const edgeDistance = window.innerWidth - e.clientX;
        
        if (edgeDistance < 15 && !isDragging && !isHoveringSidebar) {
            isHoveringSidebar = true;
            sidebar.classList.add('active');
        } 
        else if (edgeDistance > 80 && !isDragging && isHoveringSidebar && e.target.id !== 'sidebar' && !sidebar.contains(e.target)) {
            isHoveringSidebar = false;
            sidebar.classList.remove('active');
        }
    });

    function handleStart(e) {
        // Asegurar que estamos trabajando con la tarjeta correcta
        card = document.getElementById('project-card');
        if (!card) {
            console.warn('No se encontró la tarjeta para iniciar el drag');
            return;
        }

        isDragging = true;

        createProjectBtn.style.transition = 'opacity 0.3s ease';
        createProjectBtn.style.opacity = '0';
        
        if (sidebar.classList.contains('active')) {
            sidebar.classList.remove('active');
        }
        
        if (e.type === 'mousedown') {
            e.preventDefault();
        }
        
        if (e.type === 'touchstart') {
            startX = e.touches[0].clientX;
            startY = e.touches[0].clientY;
        } else {
            startX = e.clientX;
            startY = e.clientY;
        }
        
        offsetX = 0;
        offsetY = 0;
        card.style.transition = 'none';
        
        console.log('Drag iniciado en:', startX, startY);
    }

    function handleMove(e) {
        if (expandedView) return;
        if (!isDragging) return;
        
        // Actualizar referencia a la tarjeta actual
        card = document.getElementById('project-card');
        if (!card) return;
        
        let currentX, currentY;
        if (e.type === 'touchmove') {
            e.preventDefault();
            currentX = e.touches[0].clientX;
            currentY = e.touches[0].clientY;
        } else {
            currentX = e.clientX;
            currentY = e.clientY;
        }
        
        offsetX = currentX - startX;
        offsetY = currentY - startY;
        
        const rotate = offsetX * 0.1;
        card.style.transform = `translate(${offsetX}px, ${offsetY}px) rotate(${rotate}deg)`;
        
        // Mostrar indicadores
        statusIndicators.style.opacity = '1';
        if (offsetX > 50) {
            likeIndicator.style.opacity = '1';
            dislikeIndicator.style.opacity = '0';
            ampliarIndicator.style.opacity = '0'; 
        } else if (offsetX < -50) {
            likeIndicator.style.opacity = '0';
            dislikeIndicator.style.opacity = '1';
            ampliarIndicator.style.opacity = '0'; 
        } else if (offsetY < -30) {
            likeIndicator.style.opacity = '0';
            dislikeIndicator.style.opacity = '0';
            ampliarIndicator.style.opacity = '1';  
        } else {
            likeIndicator.style.opacity = '0';
            dislikeIndicator.style.opacity = '0';
            ampliarIndicator.style.opacity = '0'; 
        }
    }

    function handleEnd() {
        if (expandedView) return;
        if (!isDragging) return;
        
        // Actualizar referencia a la tarjeta actual
        card = document.getElementById('project-card');
        if (!card) return;
        
        isDragging = false;

        createProjectBtn.style.transition = 'opacity 0.3s ease';
        createProjectBtn.style.opacity = '1';
        
        card.style.transition = 'transform 0.5s ease';
        statusIndicators.style.opacity = '0';
        
        console.log('Drag terminado con offset:', offsetX, offsetY);
        
        if (offsetX > 100) {
            card.style.transform = `translate(${window.innerWidth}px, ${offsetY}px) rotate(30deg)`;
            setTimeout(() => {
                resetCard();
                handleSwipe('right');
            }, 500);
        } else if (offsetX < -100) {
            card.style.transform = `translate(-${window.innerWidth}px, ${offsetY}px) rotate(-30deg)`;
            setTimeout(() => {
                resetCard();
                handleSwipe('left');
            }, 500);
        } else if (offsetY < -100) {
            card.style.transition = 'transform 0.5s ease';
            card.style.transform = `translate(0px, -${window.innerHeight}px) rotate(0deg)`;

            setTimeout(() => {
                expandedCard.classList.remove('hidden');
                expandedCard.classList.add('visible');
                expandedView = true;
                card.style.transition = 'none';
                card.style.opacity = '0';
                card.style.transform = 'translate(0, 0) rotate(0deg)';
                card.style.transform = 'translate(0, 0)';
                card.style.transition = 'opacity 0.3s ease';
                card.style.opacity = '1';
            }, 500);
        } else {
            card.style.transform = 'translate(0, 0) rotate(0deg)';
        }
    }

    function resetCard() {
        card = document.getElementById('project-card');
        if (!card) return;
        
        card.style.transition = 'none';
        card.style.opacity = '0';
        card.style.transform = 'translate(0, 0) rotate(0deg)';
        
        setTimeout(() => {
            card.style.transition = 'opacity 0.3s ease';
            card.style.opacity = '1';
        }, 50);
    }

    closeExpanded?.addEventListener('click', () => {
        expandedCard.classList.remove('visible');
        setTimeout(() => {
            expandedCard.classList.add('hidden');
            expandedView = false;
        }, 400);
    });

    function handleSwipe(direction) {
        if (window.projectsManager) {
            window.projectsManager.handleCardSwipe(direction);
        }
    }

    // Hacer funciones disponibles globalmente
    window.handleSwipe = handleSwipe;
    window.setupSwipeHandlers = setupSwipeHandlers;
});