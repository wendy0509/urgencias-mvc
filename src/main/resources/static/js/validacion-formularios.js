/*
 * Validación en tiempo real de formularios.
 *
 * - Muestra los errores en rojo mientras el usuario escribe.
 * - Impide escribir caracteres o valores no permitidos (letras en documento,
 *   más dígitos de los permitidos, textos más largos que el máximo, etc.).
 * - Usa los mismos mensajes que las anotaciones de Bean Validation de las
 *   entidades. La validación del servidor (@Valid) se mantiene como respaldo.
 *
 * Reglas completas por campo: docs/09-reglas-de-campos.md
 *
 * Uso: agregar data-validar al <form> y, en cada campo, los atributos:
 *
 *   Validación (muestra el mensaje):
 *   data-requerido="mensaje"         campo obligatorio
 *   data-min-largo="5"               longitud mínima (mensaje en data-msg-largo)
 *   data-min / data-max              rango numérico (mensajes en data-msg-min / data-msg-max)
 *   data-email="mensaje"             formato de correo
 *
 *   Bloqueo al escribir o pegar (el carácter no entra y se muestra el mensaje):
 *   data-max-largo="100"             longitud máxima (mensaje en data-msg-max-largo)
 *   data-solo-numeros="mensaje"      solo dígitos 0-9
 *   data-solo-letras="mensaje"       solo letras (con tildes y ñ), espacios, punto, guion y apóstrofo
 *   data-max-digitos="3"             cantidad máxima de dígitos (mensaje en data-msg-digitos)
 *   data-bloquear-rango="mensaje"    no deja escribir valores fuera de data-min / data-max
 *
 *   Ayuda visual:
 *   data-contador                    muestra "usados / máximo" bajo el campo (requiere data-max-largo)
 */
(function () {
    'use strict';

    var EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    var DIGITOS = /^\d+$/;
    var NO_DIGITO = /\D/g;
    var NO_LETRA = /[^\p{L}\s.'-]/gu;
    var SOLO_LETRAS = /^[\p{L}\s.'-]+$/u;

    function mensajeMaxLargo(d) {
        return d.msgMaxLargo || ('Máximo ' + d.maxLargo + ' caracteres');
    }

    // Reglas que se evalúan sobre el valor completo (al escribir, al salir del campo y al guardar).
    function validarCampo(campo) {
        var d = campo.dataset;
        var valor = (campo.value || '').trim();

        if (valor === '') {
            return d.requerido || '';
        }
        if (d.soloNumeros !== undefined && !DIGITOS.test(valor)) {
            return d.soloNumeros;
        }
        if (d.soloLetras !== undefined && !SOLO_LETRAS.test(valor)) {
            return d.soloLetras;
        }
        if (d.min !== undefined && Number(valor) < Number(d.min)) {
            return d.msgMin;
        }
        if (d.max !== undefined && Number(valor) > Number(d.max)) {
            return d.msgMax;
        }
        if (d.minLargo !== undefined && valor.length < Number(d.minLargo)) {
            return d.msgLargo;
        }
        if (d.maxLargo !== undefined && valor.length > Number(d.maxLargo)) {
            return mensajeMaxLargo(d);
        }
        if (d.email !== undefined && !EMAIL.test(valor)) {
            return d.email;
        }
        return '';
    }

    // Impide que entren caracteres o valores no permitidos. Devuelve el aviso a mostrar, o ''.
    function filtrarEntrada(campo, previo) {
        var d = campo.dataset;
        var valor = campo.value;
        var aviso = '';

        if (d.soloNumeros !== undefined && NO_DIGITO.test(valor)) {
            valor = valor.replace(NO_DIGITO, '');
            aviso = d.soloNumeros;
        }
        if (d.soloLetras !== undefined && NO_LETRA.test(valor)) {
            valor = valor.replace(NO_LETRA, '');
            aviso = d.soloLetras;
        }
        if (d.maxDigitos !== undefined && valor.length > Number(d.maxDigitos)) {
            valor = valor.slice(0, Number(d.maxDigitos));
            aviso = d.msgDigitos;
        }
        if (d.maxLargo !== undefined && valor.length > Number(d.maxLargo)) {
            valor = valor.slice(0, Number(d.maxLargo));
            aviso = mensajeMaxLargo(d);
        }
        if (d.bloquearRango !== undefined && valor !== '' &&
                (Number(valor) < Number(d.min) || Number(valor) > Number(d.max))) {
            valor = (previo === '' || DIGITOS.test(previo)) ? previo : '';
            aviso = d.bloquearRango;
        }
        // Las expresiones con /g guardan estado entre llamadas a test()
        NO_DIGITO.lastIndex = 0;
        NO_LETRA.lastIndex = 0;

        if (valor !== campo.value) {
            campo.value = valor;
            campo.setSelectionRange(valor.length, valor.length);
        }
        return aviso;
    }

    function grupoDe(campo) {
        return campo.closest('.mb-3') || campo.parentElement;
    }

    // Reutiliza el div de error que pinta el servidor (th:errors) o crea uno nuevo.
    function contenedorError(campo) {
        var grupo = grupoDe(campo);
        var error = grupo.querySelector('div.text-danger');
        if (!error) {
            error = document.createElement('div');
            error.className = 'text-danger';
            grupo.appendChild(error);
        }
        error.setAttribute('aria-live', 'polite');
        return error;
    }

    function actualizarContador(campo) {
        var contador = campo._contador;
        if (!contador) {
            return;
        }
        var max = Number(campo.dataset.maxLargo);
        var usados = campo.value.length;
        contador.textContent = usados + ' / ' + max;
        contador.classList.toggle('text-danger', usados >= max);
        contador.classList.toggle('text-muted', usados < max);
    }

    function crearContador(campo) {
        var contador = document.createElement('small');
        contador.className = 'd-block text-end text-muted';
        contador.setAttribute('aria-hidden', 'true');
        campo.insertAdjacentElement('afterend', contador);
        campo._contador = contador;
        actualizarContador(campo);
    }

    function pintar(campo, mensaje) {
        var error = contenedorError(campo);
        error.textContent = mensaje;
        error.style.display = mensaje ? '' : 'none';
        campo.classList.toggle('is-invalid', !!mensaje);
        campo.classList.toggle('is-valid', !mensaje && campo.value.trim() !== '');
        campo.setAttribute('aria-invalid', mensaje ? 'true' : 'false');
        actualizarContador(campo);
        return !mensaje;
    }

    function iniciar(form) {
        var campos = form.querySelectorAll(
            '[data-requerido], [data-min-largo], [data-max-largo], [data-min], [data-max], ' +
            '[data-email], [data-solo-numeros], [data-solo-letras]');
        form.setAttribute('novalidate', 'novalidate');

        campos.forEach(function (campo) {
            var d = campo.dataset;
            var tocado = false;
            var evento = campo.tagName === 'SELECT' ? 'change' : 'input';
            var previo = campo.value;

            if (d.soloNumeros !== undefined) {
                campo.setAttribute('inputmode', 'numeric');
            }
            if (d.maxLargo !== undefined && d.contador !== undefined) {
                crearContador(campo);
            }

            campo.addEventListener(evento, function () {
                tocado = true;
                var aviso = filtrarEntrada(campo, previo);
                previo = campo.value;
                pintar(campo, aviso || validarCampo(campo));
            });
            campo.addEventListener('blur', function () {
                if (tocado || campo.value.trim() !== '') {
                    pintar(campo, validarCampo(campo));
                }
            });
        });

        form.addEventListener('submit', function (e) {
            var primeroInvalido = null;
            campos.forEach(function (campo) {
                if (!pintar(campo, validarCampo(campo)) && !primeroInvalido) {
                    primeroInvalido = campo;
                }
            });
            if (primeroInvalido) {
                e.preventDefault();
                primeroInvalido.focus();
            }
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll('form[data-validar]').forEach(iniciar);
    });
})();
