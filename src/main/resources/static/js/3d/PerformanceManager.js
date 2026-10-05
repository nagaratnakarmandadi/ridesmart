/**
 * RideSmart 3D - Performance & WebGL Capability Manager
 */
class PerformanceManager {
    constructor() {
        this.isWebGlSupported = this.checkWebGLSupport();
        this.prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        this.fps = 60;
        this.frameCount = 0;
        this.lastTime = performance.now();
        this.lowFpsCount = 0;
        this.tier = 'HIGH'; // HIGH, MEDIUM, LOW
    }

    checkWebGLSupport() {
        try {
            const canvas = document.createElement('canvas');
            return !!(window.WebGLRenderingContext && 
                (canvas.getContext('webgl') || canvas.getContext('experimental-webgl')));
        } catch (e) {
            return false;
        }
    }

    getOptimalPixelRatio() {
        if (this.tier === 'LOW') return 1;
        if (this.tier === 'MEDIUM') return Math.min(window.devicePixelRatio, 1.25);
        return Math.min(window.devicePixelRatio, 2);
    }

    shouldEnableShadows() {
        return this.tier !== 'LOW' && !this.isMobile();
    }

    isMobile() {
        return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) || window.innerWidth < 768;
    }

    tick() {
        this.frameCount++;
        const now = performance.now();
        const delta = now - this.lastTime;

        if (delta >= 1000) {
            this.fps = Math.round((this.frameCount * 1000) / delta);
            this.frameCount = 0;
            this.lastTime = now;

            if (this.fps < 30) {
                this.lowFpsCount++;
                if (this.lowFpsCount > 3 && this.tier === 'HIGH') {
                    this.tier = 'MEDIUM';
                    console.warn('[RideSmart 3D] Downgrading rendering tier to MEDIUM due to FPS drops');
                } else if (this.lowFpsCount > 6 && this.tier === 'MEDIUM') {
                    this.tier = 'LOW';
                    console.warn('[RideSmart 3D] Downgrading rendering tier to LOW');
                }
            } else {
                if (this.lowFpsCount > 0) this.lowFpsCount--;
            }
        }
    }
}

window.RideSmartPerformance = new PerformanceManager();
