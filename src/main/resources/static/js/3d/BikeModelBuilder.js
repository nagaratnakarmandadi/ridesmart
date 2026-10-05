/**
 * RideSmart 3D - High-Precision Motorcycle Model Builder & GLTF/GLB Loader
 */
class BikeModelBuilder {
    constructor(materialController) {
        this.materialController = materialController;
    }

    createDefaultMotorcycle() {
        const bikeGroup = new THREE.Group();
        bikeGroup.name = "RideSmart_Motorcycle_Hero";

        // Material Definitions (Photorealistic PBR)
        const paintMaterial = new THREE.MeshStandardMaterial({
            color: 0x18181B,
            metalness: 0.8,
            roughness: 0.35,
            name: "BodyPaintMaterial"
        });
        if (this.materialController) {
            this.materialController.registerPaintMaterial(paintMaterial);
        }

        const chromeMaterial = new THREE.MeshStandardMaterial({
            color: 0xE2E8F0,
            metalness: 0.95,
            roughness: 0.1,
            name: "ChromeMaterial"
        });

        const darkEngineMaterial = new THREE.MeshStandardMaterial({
            color: 0x27272A,
            metalness: 0.7,
            roughness: 0.5,
            name: "EngineMaterial"
        });

        const rubberTireMaterial = new THREE.MeshStandardMaterial({
            color: 0x18181B,
            metalness: 0.1,
            roughness: 0.9,
            name: "TireMaterial"
        });

        const leatherSeatMaterial = new THREE.MeshStandardMaterial({
            color: 0x0F172A,
            metalness: 0.05,
            roughness: 0.7,
            name: "SeatMaterial"
        });

        const glassMaterial = new THREE.MeshPhysicalMaterial({
            color: 0xFFFFFF,
            transparent: true,
            opacity: 0.4,
            roughness: 0.1,
            transmission: 0.9,
            name: "GlassMaterial"
        });

        const headlightEmissive = new THREE.MeshStandardMaterial({
            color: 0xE0F2FE,
            emissive: 0x38BDF8,
            emissiveIntensity: 1.5,
            metalness: 0.2,
            roughness: 0.2
        });

        // 1. Frame / Chassis (Double Cradle Tube)
        const frameGroup = new THREE.Group();
        const mainTubeGeo = new THREE.CylinderGeometry(0.06, 0.06, 2.4, 16);
        const mainTube = new THREE.Mesh(mainTubeGeo, darkEngineMaterial);
        mainTube.rotation.z = Math.PI / 4;
        mainTube.position.set(0, 0.75, 0);
        frameGroup.add(mainTube);

        const subFrameGeo = new THREE.CylinderGeometry(0.04, 0.04, 1.4, 16);
        const subFrame = new THREE.Mesh(subFrameGeo, darkEngineMaterial);
        subFrame.rotation.z = -Math.PI / 6;
        subFrame.position.set(-0.8, 0.95, 0);
        frameGroup.add(subFrame);
        bikeGroup.add(frameGroup);

        // 2. Fuel Tank (Sculpted Metallic Body)
        const tankShape = new THREE.Shape();
        tankShape.moveTo(0, 0);
        tankShape.lineTo(0.9, 0.2);
        tankShape.lineTo(1.1, 0.5);
        tankShape.lineTo(0.3, 0.7);
        tankShape.lineTo(-0.4, 0.4);
        tankShape.lineTo(0, 0);

        const extrudeSettings = { depth: 0.45, bevelEnabled: true, bevelSegments: 5, steps: 2, bevelSize: 0.08, bevelThickness: 0.08 };
        const tankGeo = new THREE.ExtrudeGeometry(tankShape, extrudeSettings);
        tankGeo.center();
        const tankMesh = new THREE.Mesh(tankGeo, paintMaterial);
        tankMesh.position.set(0.15, 1.05, 0);
        tankMesh.rotation.y = Math.PI / 2;
        tankMesh.castShadow = true;
        bikeGroup.add(tankMesh);

        // 3. Ergonomic Leather Seat
        const seatGeo = new THREE.BoxGeometry(0.9, 0.14, 0.38);
        const seatMesh = new THREE.Mesh(seatGeo, leatherSeatMaterial);
        seatMesh.position.set(-0.55, 0.96, 0);
        seatMesh.castShadow = true;
        bikeGroup.add(seatMesh);

        // 4. V-Twin Engine Block & Cooling Fins
        const engineGroup = new THREE.Group();
        const blockGeo = new THREE.BoxGeometry(0.65, 0.55, 0.42);
        const engineBlock = new THREE.Mesh(blockGeo, darkEngineMaterial);
        engineBlock.position.set(0, 0.52, 0);
        engineBlock.castShadow = true;
        engineGroup.add(engineBlock);

        // Cylinders
        const cylGeo = new THREE.CylinderGeometry(0.16, 0.16, 0.45, 16);
        const cyl1 = new THREE.Mesh(cylGeo, chromeMaterial);
        cyl1.position.set(0.18, 0.68, 0);
        cyl1.rotation.z = -Math.PI / 12;
        engineGroup.add(cyl1);

        const cyl2 = new THREE.Mesh(cylGeo, chromeMaterial);
        cyl2.position.set(-0.18, 0.68, 0);
        cyl2.rotation.z = Math.PI / 12;
        engineGroup.add(cyl2);
        bikeGroup.add(engineGroup);

        // 5. Dual Chrome Exhaust Pipe
        const exhaustCurve = new THREE.CatmullRomCurve3([
            new THREE.Vector3(0.1, 0.5, 0.2),
            new THREE.Vector3(0.3, 0.35, 0.24),
            new THREE.Vector3(-0.6, 0.35, 0.25),
            new THREE.Vector3(-1.3, 0.4, 0.25)
        ]);
        const exhaustGeo = new THREE.TubeGeometry(exhaustCurve, 32, 0.06, 16, false);
        const exhaustMesh = new THREE.Mesh(exhaustGeo, chromeMaterial);
        exhaustMesh.castShadow = true;
        bikeGroup.add(exhaustMesh);

        // 6. Wheels (Front & Rear Alloy Wheels with Discs & Tires)
        const createWheel = (radius, width, posX, isFront) => {
            const wheelGroup = new THREE.Group();
            
            // Outer Tire
            const tireGeo = new THREE.TorusGeometry(radius, width, 24, 48);
            const tireMesh = new THREE.Mesh(tireGeo, rubberTireMaterial);
            tireMesh.castShadow = true;
            wheelGroup.add(tireMesh);

            // Rim / Spokes
            const rimGeo = new THREE.CylinderGeometry(radius * 0.75, radius * 0.75, width * 0.8, 24);
            const rimMesh = new THREE.Mesh(rimGeo, chromeMaterial);
            rimMesh.rotation.x = Math.PI / 2;
            wheelGroup.add(rimMesh);

            // 5-Spoke Alloy Pattern
            for (let i = 0; i < 5; i++) {
                const spokeGeo = new THREE.BoxGeometry(radius * 1.5, 0.03, width * 0.4);
                const spoke = new THREE.Mesh(spokeGeo, darkEngineMaterial);
                spoke.rotation.z = (i * Math.PI * 2) / 5;
                wheelGroup.add(spoke);
            }

            // Disc Brake
            const discGeo = new THREE.CylinderGeometry(radius * 0.65, radius * 0.65, 0.02, 32);
            const discMesh = new THREE.Mesh(discGeo, chromeMaterial);
            discMesh.rotation.x = Math.PI / 2;
            discMesh.position.z = width * 0.6;
            wheelGroup.add(discMesh);

            wheelGroup.position.set(posX, radius, 0);
            return wheelGroup;
        };

        const frontWheel = createWheel(0.42, 0.12, 1.25, true);
        const rearWheel = createWheel(0.42, 0.14, -1.15, false);
        bikeGroup.add(frontWheel);
        bikeGroup.add(rearWheel);

        // 7. Front Suspension Forks & Handlebars
        const forkGroup = new THREE.Group();
        const forkGeo = new THREE.CylinderGeometry(0.035, 0.035, 1.1, 16);
        const leftFork = new THREE.Mesh(forkGeo, chromeMaterial);
        leftFork.position.set(1.15, 0.78, 0.14);
        leftFork.rotation.z = -Math.PI / 8;
        forkGroup.add(leftFork);

        const rightFork = new THREE.Mesh(forkGeo, chromeMaterial);
        rightFork.position.set(1.15, 0.78, -0.14);
        rightFork.rotation.z = -Math.PI / 8;
        forkGroup.add(rightFork);

        // Handlebars
        const barGeo = new THREE.CylinderGeometry(0.025, 0.025, 0.75, 16);
        const handlebar = new THREE.Mesh(barGeo, darkEngineMaterial);
        handlebar.rotation.x = Math.PI / 2;
        handlebar.position.set(0.96, 1.25, 0);
        forkGroup.add(handlebar);
        bikeGroup.add(forkGroup);

        // 8. Headlight Unit & Windshield
        const lightGeo = new THREE.CylinderGeometry(0.14, 0.14, 0.12, 24);
        const lightMesh = new THREE.Mesh(lightGeo, headlightEmissive);
        lightMesh.rotation.z = Math.PI / 2;
        lightMesh.position.set(1.28, 1.15, 0);
        bikeGroup.add(lightMesh);

        const visorGeo = new THREE.PlaneGeometry(0.35, 0.4);
        const visorMesh = new THREE.Mesh(visorGeo, glassMaterial);
        visorMesh.position.set(1.18, 1.38, 0);
        visorMesh.rotation.y = Math.PI / 2;
        visorMesh.rotation.x = Math.PI / 6;
        bikeGroup.add(visorMesh);

        return bikeGroup;
    }
}

window.RideSmartBikeBuilder = BikeModelBuilder;
