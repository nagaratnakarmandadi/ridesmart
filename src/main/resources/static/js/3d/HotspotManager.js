/**
 * RideSmart 3D - Interactive Component Hotspot & Specification Overlay Manager
 */
class HotspotManager {
    constructor(camera, container, cameraController) {
        this.camera = camera;
        this.container = container;
        this.cameraController = cameraController;
        this.hotspots = [
            {
                id: 'engine',
                title: 'High-Output Engine',
                badge: 'PERFORMANCE',
                desc: '350cc - 500cc tuned engine optimized for city commuting & highway touring.',
                position: { x: 0, y: 0.65, z: 0.1 },
                preset: 'ENGINE'
            },
            {
                id: 'brakes',
                title: 'Dual Disc ABS Brakes',
                badge: 'SAFETY',
                desc: 'Responsive dual-channel ABS disc braking system for maximum stopping grip.',
                position: { x: 0.8, y: 0.45, z: 1.4 },
                preset: 'BRAKES'
            },
            {
                id: 'suspension',
                title: 'Monoshock Suspension',
                badge: 'COMFORT',
                desc: 'Adjustable rear monoshock and telescopic front forks designed for Indian roads.',
                position: { x: -0.8, y: 0.7, z: -0.8 },
                preset: 'REAR_34'
            },
            {
                id: 'cockpit',
                title: 'Smart Digital Console',
                badge: 'TELEMETRY',
                desc: 'Integrated GPS tracking, speed, fuel telemetry, and digital trip metrics.',
                position: { x: 0, y: 1.25, z: 0.5 },
                preset: 'COCKPIT'
            }
        ];
        this.elements = [];
        this.activeHotspotId = null;
    }

    renderHotspotDOM(overlayContainer) {
        if (!overlayContainer) return;
        overlayContainer.innerHTML = '';
        this.elements = [];

        this.hotspots.forEach(h => {
            const btn = document.createElement('button');
            btn.className = 'hotspot-pin-btn';
            btn.setAttribute('type', 'button');
            btn.setAttribute('data-id', h.id);
            btn.innerHTML = `<span class="hotspot-pulse"></span><i class="bi bi-info-circle-fill"></i>`;
            
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                this.selectHotspot(h);
            });

            overlayContainer.appendChild(btn);
            this.elements.push({ config: h, element: btn });
        });
    }

    selectHotspot(hotspot) {
        this.activeHotspotId = hotspot.id;
        if (this.cameraController) {
            this.cameraController.setPreset(hotspot.preset);
        }

        const infoCard = document.getElementById('hotspotInfoCard');
        if (infoCard) {
            infoCard.innerHTML = `
                <div class="glass-card p-3 shadow-lg border-indigo fade-in">
                    <div class="d-flex align-items-center justify-content-between mb-2">
                        <span class="badge bg-indigo text-white fw-bold tiny">${hotspot.badge}</span>
                        <button type="button" class="btn-close btn-close-white tiny" onclick="document.getElementById('hotspotInfoCard').style.display='none'"></button>
                    </div>
                    <h6 class="fw-bold text-white mb-1">${hotspot.title}</h6>
                    <p class="small text-slate-300 mb-0">${hotspot.desc}</p>
                </div>
            `;
            infoCard.style.display = 'block';
        }
    }

    updatePositions(canvasWidth, canvasHeight) {
        if (!this.camera || !canvasWidth || !canvasHeight) return;

        const tempVec = new THREE.Vector3();

        this.elements.forEach(item => {
            const pos = item.config.position;
            tempVec.set(pos.x, pos.y, pos.z);
            tempVec.project(this.camera);

            // Check if point is behind camera
            if (tempVec.z > 1) {
                item.element.style.display = 'none';
                return;
            }

            const x = (tempVec.x * 0.5 + 0.5) * canvasWidth;
            const y = (-(tempVec.y * 0.5) + 0.5) * canvasHeight;

            item.element.style.display = 'flex';
            item.element.style.left = `${x}px`;
            item.element.style.top = `${y}px`;
        });
    }
}

window.RideSmartHotspots = HotspotManager;
