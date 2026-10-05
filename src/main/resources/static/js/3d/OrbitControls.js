/**
 * Three.js Standalone OrbitControls (r128 Global Compatible)
 */
(function () {
    if (typeof THREE === 'undefined') return;

    THREE.OrbitControls = function (object, domElement) {
        this.object = object;
        this.domElement = (domElement !== undefined) ? domElement : document;

        // API
        this.enabled = true;
        this.target = new THREE.Vector3();

        this.minDistance = 0;
        this.maxDistance = Infinity;

        this.minPolarAngle = 0;
        this.maxPolarAngle = Math.PI;

        this.enableDamping = false;
        this.dampingFactor = 0.05;

        this.enableZoom = true;
        this.zoomSpeed = 1.0;

        this.enableRotate = true;
        this.rotateSpeed = 1.0;

        this.autoRotate = false;
        this.autoRotateSpeed = 2.0;

        // Internals
        var scope = this;
        var changeEvent = { type: 'change' };
        var startEvent = { type: 'start' };
        var endEvent = { type: 'end' };

        var STATE = { NONE: -1, ROTATE: 0, DOLLY: 1, PAN: 2, TOUCH_ROTATE: 3, TOUCH_DOLLY_PAN: 4 };
        var state = STATE.NONE;

        var spherical = new THREE.Spherical();
        var sphericalDelta = new THREE.Spherical();

        var scale = 1;
        var panOffset = new THREE.Vector3();
        var zoomChanged = false;

        var rotateStart = new THREE.Vector2();
        var rotateEnd = new THREE.Vector2();
        var rotateDelta = new THREE.Vector2();

        var dollyStart = new THREE.Vector2();
        var dollyEnd = new THREE.Vector2();
        var dollyDelta = new THREE.Vector2();

        this.update = function () {
            var offset = new THREE.Vector3();

            // position of camera relative to target
            var position = scope.object.position;

            offset.copy(position).sub(scope.target);

            // rotate offset to "y-axis-up" space
            spherical.setFromVector3(offset);

            if (scope.autoRotate && state === STATE.NONE) {
                scope.rotateLeft(getAutoRotationAngle());
            }

            spherical.theta += sphericalDelta.theta;
            spherical.phi += sphericalDelta.phi;

            // restrict phi to be between desired limits
            spherical.phi = Math.max(scope.minPolarAngle, Math.min(scope.maxPolarAngle, spherical.phi));

            spherical.makeSafe();

            spherical.radius *= scale;

            // restrict radius to be between desired limits
            spherical.radius = Math.max(scope.minDistance, Math.min(scope.maxDistance, spherical.radius));

            // move target to panned location
            scope.target.add(panOffset);

            offset.setFromSpherical(spherical);

            position.copy(scope.target).add(offset);

            scope.object.lookAt(scope.target);

            if (scope.enableDamping === true) {
                sphericalDelta.theta *= (1 - scope.dampingFactor);
                sphericalDelta.phi *= (1 - scope.dampingFactor);

                panOffset.x *= (1 - scope.dampingFactor);
                panOffset.y *= (1 - scope.dampingFactor);
                panOffset.z *= (1 - scope.dampingFactor);
            } else {
                sphericalDelta.set(0, 0, 0);
                panOffset.set(0, 0, 0);
            }

            scale = 1;

            return true;
        };

        function getAutoRotationAngle() {
            return 2 * Math.PI / 60 / 60 * scope.autoRotateSpeed;
        }

        this.rotateLeft = function (angle) {
            sphericalDelta.theta -= angle;
        };

        this.rotateUp = function (angle) {
            sphericalDelta.phi -= angle;
        };

        // Event Listeners
        function onMouseDown(event) {
            if (scope.enabled === false) return;

            event.preventDefault();

            if (event.button === 0) {
                state = STATE.ROTATE;
                rotateStart.set(event.clientX, event.clientY);
            }

            if (state !== STATE.NONE) {
                document.addEventListener('mousemove', onMouseMove, false);
                document.addEventListener('mouseup', onMouseUp, false);
                scope.dispatchEvent(startEvent);
            }
        }

        function onMouseMove(event) {
            if (scope.enabled === false) return;

            event.preventDefault();

            if (state === STATE.ROTATE) {
                rotateEnd.set(event.clientX, event.clientY);
                rotateDelta.subVectors(rotateEnd, rotateStart).multiplyScalar(scope.rotateSpeed * 0.005);

                var element = scope.domElement;
                scope.rotateLeft(2 * Math.PI * rotateDelta.x / element.clientHeight);
                scope.rotateUp(2 * Math.PI * rotateDelta.y / element.clientHeight);

                rotateStart.copy(rotateEnd);
            }

            scope.update();
        }

        function onMouseUp() {
            if (scope.enabled === false) return;

            document.removeEventListener('mousemove', onMouseMove, false);
            document.removeEventListener('mouseup', onMouseUp, false);

            scope.dispatchEvent(endEvent);

            state = STATE.NONE;
        }

        function onMouseWheel(event) {
            if (scope.enabled === false || scope.enableZoom === false) return;

            event.preventDefault();

            if (event.deltaY < 0) {
                scale /= 0.95;
            } else if (event.deltaY > 0) {
                scale *= 0.95;
            }

            scope.update();
        }

        // Touch handlers for mobile
        function onTouchStart(event) {
            if (scope.enabled === false) return;

            if (event.touches.length === 1) {
                state = STATE.TOUCH_ROTATE;
                rotateStart.set(event.touches[0].pageX, event.touches[0].pageY);
            }
        }

        function onTouchMove(event) {
            if (scope.enabled === false) return;

            event.preventDefault();

            if (state === STATE.TOUCH_ROTATE) {
                rotateEnd.set(event.touches[0].pageX, event.touches[0].pageY);
                rotateDelta.subVectors(rotateEnd, rotateStart).multiplyScalar(scope.rotateSpeed * 0.005);

                var element = scope.domElement;
                scope.rotateLeft(2 * Math.PI * rotateDelta.x / element.clientHeight);
                scope.rotateUp(2 * Math.PI * rotateDelta.y / element.clientHeight);

                rotateStart.copy(rotateEnd);
                scope.update();
            }
        }

        function onTouchEnd() {
            if (scope.enabled === false) return;
            state = STATE.NONE;
        }

        this.domElement.addEventListener('mousedown', onMouseDown, false);
        this.domElement.addEventListener('wheel', onMouseWheel, { passive: false });
        this.domElement.addEventListener('touchstart', onTouchStart, { passive: false });
        this.domElement.addEventListener('touchmove', onTouchMove, { passive: false });
        this.domElement.addEventListener('touchend', onTouchEnd, false);

        // EventDispatcher methods
        this.addEventListener = THREE.EventDispatcher.prototype.addEventListener;
        this.hasEventListener = THREE.EventDispatcher.prototype.hasEventListener;
        this.removeEventListener = THREE.EventDispatcher.prototype.removeEventListener;
        this.dispatchEvent = THREE.EventDispatcher.prototype.dispatchEvent;

        this.update();
    };
})();
