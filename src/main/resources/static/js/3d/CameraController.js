/**
 * RideSmart 3D - Smooth Interpolated Camera & Viewport Preset Controller
 */
class CameraController {
    constructor(camera, controls) {
        this.camera = camera;
        this.controls = controls;
        this.presets = {
            FRONT_34: { pos: { x: 3.2, y: 1.8, z: 4.2 }, target: { x: 0, y: 0.8, z: 0 }, label: "3/4 Front View" },
            SIDE_PROFILE: { pos: { x: 0, y: 1.2, z: 5.2 }, target: { x: 0, y: 0.8, z: 0 }, label: "Side Profile" },
            REAR_34: { pos: { x: -3.4, y: 1.6, z: -3.8 }, target: { x: 0, y: 0.8, z: 0 }, label: "3/4 Rear View" },
            COCKPIT: { pos: { x: 0.1, y: 1.4, z: 0.6 }, target: { x: 0, y: 1.1, z: -0.8 }, label: "Handlebar & Cockpit" },
            ENGINE: { pos: { x: 1.2, y: 0.7, z: 1.2 }, target: { x: 0, y: 0.6, z: 0 }, label: "High-Output Engine" },
            BRAKES: { pos: { x: 1.8, y: 0.5, z: 2.2 }, target: { x: 0.8, y: 0.4, z: 1.4 }, label: "Dual Disc Brakes" }
        };
        this.isAnimating = false;
    }

    setPreset(presetKey, durationMs = 1200) {
        const targetConfig = this.presets[presetKey];
        if (!targetConfig) return;

        const startPos = { x: this.camera.position.x, y: this.camera.position.y, z: this.camera.position.z };
        const startTarget = { x: this.controls.target.x, y: this.controls.target.y, z: this.controls.target.z };

        const endPos = targetConfig.pos;
        const endTarget = targetConfig.target;

        const startTime = performance.now();
        this.isAnimating = true;

        const animateCamera = (currentTime) => {
            const elapsed = currentTime - startTime;
            const progress = Math.min(elapsed / durationMs, 1);
            // Cubic ease-out formula
            const ease = 1 - Math.pow(1 - progress, 3);

            this.camera.position.x = startPos.x + (endPos.x - startPos.x) * ease;
            this.camera.position.y = startPos.y + (endPos.y - startPos.y) * ease;
            this.camera.position.z = startPos.z + (endPos.z - startPos.z) * ease;

            this.controls.target.x = startTarget.x + (endTarget.x - startTarget.x) * ease;
            this.controls.target.y = startTarget.y + (endTarget.y - startTarget.y) * ease;
            this.controls.target.z = startTarget.z + (endTarget.z - startTarget.z) * ease;

            this.controls.update();

            if (progress < 1) {
                requestAnimationFrame(animateCamera);
            } else {
                this.isAnimating = false;
            }
        };

        requestAnimationFrame(animateCamera);
    }
}

window.RideSmartCamera = CameraController;
