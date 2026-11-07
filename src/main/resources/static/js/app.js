/**
 * Lógica del panel (index.html).
 *
 * Cada <form> tiene:
 *   data-ruta-base  -> ruta de la API (p. ej. /api/materias)
 *   data-ruta-id    -> (opcional) nombre del campo que va dentro de la ruta (/api/materias/{id})
 * Los demás campos con valor se envían como parámetros de consulta (?nombre=...&creditos=...).
 *
 * Botones:
 *   "Ver resultado" -> abre resultado.html?url=<ruta> (respuesta en tabla)
 *   "Ver API"       -> abre la ruta real en una pestaña nueva (JSON sin formato)
 */
(function () {
  'use strict';

  /** Construye la ruta a consultar a partir de los campos del formulario. */
  function construirRuta(form) {
    let ruta = form.dataset.rutaBase;
    const campoId = form.dataset.rutaId;
    const params = new URLSearchParams();

    Array.from(form.elements).forEach(function (campo) {
      if (!campo.name) { return; }
      const valor = campo.value.trim();
      if (valor === '') { return; }
      if (campo.name === campoId) {
        ruta += '/' + encodeURIComponent(valor);
      } else {
        params.append(campo.name, valor);
      }
    });

    const consulta = params.toString();
    return consulta ? ruta + '?' + consulta : ruta;
  }

  /** Muestra en la tarjeta la ruta que se consultará (se actualiza al escribir). */
  function actualizarVistaPrevia(form) {
    const etiqueta = form.closest('.tarjeta').querySelector('[data-ruta]');
    if (!etiqueta) { return; }
    const campoId = form.dataset.rutaId;
    const hayId = campoId && form.elements[campoId] && form.elements[campoId].value.trim() !== '';
    etiqueta.textContent = (campoId && !hayId) ? form.dataset.rutaBase + '/{' + campoId + '}' : construirRuta(form);
  }

  document.querySelectorAll('form[data-ruta-base]').forEach(function (form) {
    form.addEventListener('input', function () { actualizarVistaPrevia(form); });
    form.addEventListener('reset', function () { setTimeout(function () { actualizarVistaPrevia(form); }, 0); });

    // Enter o "Ver resultado": página con la respuesta ordenada.
    form.addEventListener('submit', function (evento) {
      evento.preventDefault();
      if (!form.reportValidity()) { return; }
      window.location.href = 'resultado.html?url=' + encodeURIComponent(construirRuta(form));
    });

    // "Ver API": la dirección real, tal cual la devuelve el servidor.
    const botonApi = form.querySelector('[data-accion="api"]');
    if (botonApi) {
      botonApi.addEventListener('click', function () {
        if (!form.reportValidity()) { return; }
        window.open(construirRuta(form), '_blank', 'noopener');
      });
    }

    actualizarVistaPrevia(form);
  });
})();
