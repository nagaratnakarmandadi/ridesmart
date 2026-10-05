/**
 * RideSmart 3D - Real-time Paint & Material Controller
 */
class MaterialController {
    constructor(scene) {
        this.scene = scene;
        this.colorPresets = {
            BLACK: { hex: 0x18181B, name: 'Matte Black', roughness: 0.35, metalness: 0.8 },
            RED: { hex: 0xDC2626, name: 'Racing Red', roughness: 0.25, metalness: 0.7 },
            WHITE: { hex: 0xF8FAFC, name: 'Pearl White', roughness: 0.2, metalness: 0.6 },
            BLUE: { hex: 0x1E3A8A, name: 'Midnight Blue', roughness: 0.3, metalness: 0.85 },
            GREY: { hex: 0x3F3F46, name: 'Graphite Grey', roughness: 0.4, metalness: 0.9 }
        };
        this.paintMaterials = [];
    }

    registerPaintMaterial(material) {
        if (material && !this.paintMaterials.includes(material)) {
            this.paintMaterials.push(material);
        }
    }

    applyColor(colorKey) {
        const config = this.colorPresets[colorKey] || this.colorPresets.BLACK;
        this.paintMaterials.forEach(mat => {
            if (mat && mat.color) {
                mat.color.setHex(config.hex);
                if (mat.roughness !== undefined) mat.roughness = config.roughness;
                if (mat.metalness !== undefined) mat.metalness = config.metalness;
                mat.needsUpdate = true;
            }
        });
        return config;
    }
}

window.RideSmartMaterials = MaterialController;
