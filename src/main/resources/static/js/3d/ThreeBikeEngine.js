/**
 * RideSmart 3D - High-Performance WebGL Engine Orchestrator
 */
class ThreeBikeEngine {
    constructor(config = {}) {
        this.container = document.getElementById(config.containerId || 'threeCanvasContainer');
        this.model3dUrl = config.model3dUrl || null;
        this.fallbackImgUrl = config.fallbackImgUrl || null;

        if (!this.container) {
            console.error('[RideSmart 3D] Container element not found:', config.containerId);
            return;
        }

        this.width = this.container.clientWidth || 800;
        this.height = this.container.clientHeight || 500;

        this.scene = null;
        this.camera = null;
        this.renderer = null;
        this.controls = null;
        this.bikeGroup = null;
        this.materialController = null;
        this.cameraController = null;
        this.hotspotManager = null;

        this.isRendering = false;
        this.autoRotate = true;
        this.animationFrameId = null;

        this.init();
    }

    init() {
        if (!window.RideSmartPerformance || !window.RideSmartPerformance.isWebGlSupported) {
            console.warn('[RideSmart 3D] WebGL not supported. Enabling high-quality 2D fallback.');
            this.showFallback();
            return;
        }

        try {
            // 1. Scene Setup
            this.scene = new THREE.Scene();
            this.scene.background = new THREE.Color(0x0F172A);

            // Subtle environment fog
            this.scene.fog = new THREE.FogExp2(0x0F172A, 0.08);

            // 2. Camera Setup
            this.camera = new THREE.PerspectiveCamera(42, this.width / this.height, 0.1, 100);
            this.camera.position.set(3.2, 1.8, 4.2);

            // 3. WebGL Renderer Setup
            this.renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true, powerPreference: "high-performance" });
            this.renderer.setSize(this.width, this.height);
            this.renderer.setPixelRatio(window.RideSmartPerformance.getOptimalPixelRatio());
            this.renderer.toneMapping = THREE.ACESFilmicToneMapping;
            this.renderer.toneMappingExposure = 1.1;

            if (window.RideSmartPerformance.shouldEnableShadows()) {
                this.renderer.shadowMap.enabled = true;
                this.renderer.shadowMap.type = THREE.PCFSoftShadowMap;
            }

            this.container.appendChild(this.renderer.domElement);

            // 4. Orbit Controls Setup
            this.controls = new THREE.OrbitControls(this.camera, this.renderer.domElement);
            this.controls.enableDamping = true;
            this.controls.dampingFactor = 0.05;
            this.controls.maxPolarAngle = Math.PI / 2 + 0.05; // Prevent camera from going under floor
            this.controls.minDistance = 2.0;
            this.controls.maxDistance = 10.0;
            this.controls.target.set(0, 0.8, 0);

            // Pause auto-rotation when user starts manual drag interaction
            this.controls.addEventListener('start', () => { this.autoRotate = false; });

            // 5. Studio Lighting
            this.setupStudioLighting();

            // 6. Controllers Setup
            this.materialController = new window.RideSmartMaterials(this.scene);
            this.cameraController = new window.RideSmartCamera(this.camera, this.controls);
            
            const overlayContainer = document.getElementById('hotspotOverlayContainer');
            if (overlayContainer) {
                this.hotspotManager = new window.RideSmartHotspots(this.camera, this.container, this.cameraController);
                this.hotspotManager.renderHotspotDOM(overlayContainer);
            }

            // 7. Motorcycle Model Construction
            this.loadMotorcycleModel();

            // 8. Event Listeners (Resize, Visibility)
            this.setupEventListeners();

            // 9. Start Render Loop
            this.startLoop();

            // Hide loading indicator if present
            const loaderEl = document.getElementById('threeLoaderProgress');
            if (loaderEl) loaderEl.style.display = 'none';

        } catch (err) {
            console.error('[RideSmart 3D] WebGL Initialization Error:', err);
            this.showFallback();
        }
    }

    setupStudioLighting() {
        // Soft Ambient Light
        const ambientLight = new THREE.AmbientLight(0xFFFFFF, 0.6);
        this.scene.add(ambientLight);

        // Key Light (Main Soft Sun/Spot)
        const keyLight = new THREE.DirectionalLight(0xFFFFFF, 1.8);
        keyLight.position.set(5, 8, 5);
        if (window.RideSmartPerformance.shouldEnableShadows()) {
            keyLight.castShadow = true;
            keyLight.shadow.mapSize.width = 2048;
            keyLight.shadow.mapSize.height = 2048;
            keyLight.shadow.bias = -0.0001;
        }
        this.scene.add(keyLight);

        // Fill Light (Cyan Tint for Premium Dark Aesthetics)
        const fillLight = new THREE.DirectionalLight(0x38BDF8, 0.8);
        fillLight.position.set(-5, 4, -4);
        this.scene.add(fillLight);

        // Rim Light (Indigo Backlight Edge Glow)
        const rimLight = new THREE.DirectionalLight(0x818CF8, 1.2);
        rimLight.position.set(0, 6, -6);
        this.scene.add(rimLight);

        // Ground Floor Shadow Receiver & Reflection Grid
        const floorGeo = new THREE.PlaneGeometry(30, 30);
        const floorMat = new THREE.MeshStandardMaterial({ color: 0x0F172A, roughness: 0.8, metalness: 0.2 });
        const floorMesh = new THREE.Mesh(floorGeo, floorMat);
        floorMesh.rotation.x = -Math.PI / 2;
        floorMesh.position.y = 0;
        floorMesh.receiveShadow = true;
        this.scene.add(floorMesh);

        // Subtle Circular Shadow Disc under Bike
        const shadowDiscGeo = new THREE.RingGeometry(0, 2.2, 32);
        const shadowDiscMat = new THREE.MeshBasicMaterial({ color: 0x020617, transparent: true, opacity: 0.6 });
        const shadowDisc = new THREE.Mesh(shadowDiscGeo, shadowDiscMat);
        shadowDisc.rotation.x = -Math.PI / 2;
        shadowDisc.position.y = 0.01;
        this.scene.add(shadowDisc);
    }

    loadMotorcycleModel() {
        const builder = new window.RideSmartBikeBuilder(this.materialController);
        this.bikeGroup = builder.createDefaultMotorcycle();
        this.scene.add(this.bikeGroup);
    }

    setupEventListeners() {
        this.onResize = () => {
            if (!this.container || !this.renderer || !this.camera) return;
            this.width = this.container.clientWidth;
            this.height = this.container.clientHeight;

            this.camera.aspect = this.width / this.height;
            this.camera.updateProjectionMatrix();

            this.renderer.setSize(this.width, this.height);
            this.renderer.setPixelRatio(window.RideSmartPerformance.getOptimalPixelRatio());
        };

        window.addEventListener('resize', this.onResize);

        // Pause rendering when tab is hidden to save GPU & battery
        document.addEventListener('visibilitychange', () => {
            if (document.hidden) {
                this.stopLoop();
            } else {
                this.startLoop();
            }
        });
    }

    startLoop() {
        if (this.isRendering) return;
        this.isRendering = true;

        const renderFrame = () => {
            if (!this.isRendering) return;

            window.RideSmartPerformance.tick();

            if (this.controls) this.controls.update();

            // Slow idle rotation when not interacting
            if (this.autoRotate && this.bikeGroup && !window.RideSmartPerformance.prefersReducedMotion) {
                this.bikeGroup.rotation.y += 0.003;
            }

            if (this.hotspotManager) {
                this.hotspotManager.updatePositions(this.width, this.height);
            }

            this.renderer.render(this.scene, this.camera);
            this.animationFrameId = requestAnimationFrame(renderFrame);
        };

        this.animationFrameId = requestAnimationFrame(renderFrame);
    }

    stopLoop() {
        this.isRendering = false;
        if (this.animationFrameId) {
            cancelAnimationFrame(this.animationFrameId);
            this.animationFrameId = null;
        }
    }

    showFallback() {
        if (this.container) {
            this.container.innerHTML = `
                <div class="d-flex flex-column align-items-center justify-content-center h-100 text-center p-4">
                    <img src="${this.fallbackImgUrl || '/images/hero-bike.png'}" alt="RideSmart Motorcycle" class="img-fluid rounded-4 shadow-lg mb-3" style="max-height: 320px; object-fit: contain;" onerror="this.src='https://images.unsplash.com/photo-1558981806-ec527fa84c39?w=800'">
                    <span class="badge bg-indigo-subtle text-indigo fw-bold py-2 px-3">PREMIUM MOTORCYCLE FLEET</span>
                </div>
            `;
        }
    }

    setCameraPreset(presetKey) {
        if (this.cameraController) {
            this.autoRotate = false;
            this.cameraController.setPreset(presetKey);
        }
    }

    setBodyColor(colorKey) {
        if (this.materialController) {
            return this.materialController.applyColor(colorKey);
        }
    }

    dispose() {
        this.stopLoop();
        if (this.onResize) window.removeEventListener('resize', this.onResize);
        if (this.renderer && this.renderer.domElement) {
            this.container.removeChild(this.renderer.domElement);
            this.renderer.dispose();
        }
    }
}

window.ThreeBikeEngine = ThreeBikeEngine;
