/*
 * Validación en tiempo real de formularios.
 *
 * Muestra los errores en rojo mientras el usuario escribe, con los mismos
 * mensajes que las anotaciones de Bean Validation de las entidades.
 * La validación del servidor (@Valid) se mantiene como respaldo.
 *
 * Uso: agregar data-validar al <form> y, en cada campo, los atributos:
 *   data-requerido="mensaje"                 campo obligatorio
 *   data-min-largo / data-max-largo          longitud de texto
 *   data-msg-largo="mensaje"                 mensaje para la longitud
 *   data-min / data-max                      rango numérico
 *   data-msg-min / data-msg-max              mensajes para el rango
 *   data-entero="mensaje"                    solo números enteros
 *   data-email="mensaje"                     formato de correo
 *   data-solo-numeros="mensaje"              solo dígitos: bloquea letras y símbolos al escribir o pegar
 */
(function () {
    'use strict';

    var EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    var ENTERO = /^-?\d+$/;
    var DIGITOS = /^\d+$/;

    function validarCampo(campo) {
        var d = campo.dataset;
        var valor = (campo.value || '').trim();

        if (valor === '') {
            return d.requerido || '';
        }
        if (d.soloNumeros !== undefined && !DIGITOS.test(valor)) {
            return d.soloNumeros;
        }
        if (d.entero !== undefined && !ENTERO.test(valor)) {
            return d.entero;
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
            return d.msgLargo;
        }
        if (d.email !== undefined && !EMAIL.test(valor)) {
            return d.email;
        }
        return '';
    }

    // Reutiliza el div de error que pinta el servidor (th:errors) o crea uno nuevo.
    function contenedorError(campo) {
        var grupo = campo.closest('.mb-3') || campo.parentElement;
        var error = grupo.querySelector('.text-danger');
        if (!error) {
            error = document.createElement('div');
            error.className = 'text-danger';
            grupo.appendChild(error);
        }
        error.setAttribute('aria-live', 'polite');
        return error;
    }

    function pintar(campo, mensaje) {
        var error = contenedorError(campo);
        error.textContent = mensaje;
        error.style.display = mensaje ? '' : 'none';
        campo.classList.toggle('is-invalid', !!mensaje);
        campo.classList.toggle('is-valid', !mensaje && campo.value.trim() !== '');
        campo.setAttribute('aria-invalid', mensaje ? 'true' : 'false');
        return !mensaje;
    }

    function iniciar(form) {
        var campos = form.querySelectorAll('[data-requerido], [data-min-largo], [data-max-largo], [data-min], [data-max], [data-email], [data-entero], [data-solo-numeros]');
        form.setAttribute('novalidate', 'novalidate');

        campos.forEach(function (campo) {
            var tocado = false;
            var evento = campo.tagName === 'SELECT' ? 'change' : 'input';
            var soloNumeros = campo.dataset.soloNumeros !== undefined;

            if (soloNumeros) {
                campo.setAttribute('inputmode', 'numeric');
            }

            campo.addEventListener(evento, function () {
                tocado = true;
                if (soloNumeros && /\D/.test(campo.value)) {
                    // Quita letras, espacios y símbolos (al escribir o al pegar) y avisa
                    var pos = campo.selectionStart - (campo.value.slice(0, campo.selectionStart).match(/\D/g) || []).length;
                    campo.value = campo.value.replace(/\D/g, '');
                    campo.setSelectionRange(pos, pos);
                    pintar(campo, campo.dataset.soloNumeros);
                    return;
                }
                pintar(campo, validarCampo(campo));
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
