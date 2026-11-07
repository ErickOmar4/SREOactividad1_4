/**
 * Lógica de resultado.html: consulta la ruta recibida en ?url=... y muestra la respuesta
 * en forma de tabla (listas), ficha (objetos) o texto.
 *
 * Seguridad: solo se aceptan rutas internas que empiecen con /api/ (nunca direcciones externas),
 * y todo el contenido se inserta con textContent para evitar inyección de HTML.
 */
(function () {
  'use strict';

  const ETIQUETAS = {
    id: 'ID', nombre: 'Nombre', creditos: 'Créditos', semestre: 'Semestre',
    estado: 'Estado', proyecto: 'Proyecto', autor: 'Autor',
    javaVersion: 'Versión de Java', puerto: 'Puerto', fecha: 'Fecha del servidor',
    timestamp: 'Fecha', status: 'Código', error: 'Error', message: 'Mensaje', path: 'Ruta'
  };

  const $ = function (id) { return document.getElementById(id); };
  const etiqueta = function (clave) { return ETIQUETAS[clave] || clave; };

  function el(etiquetaHtml, texto, clase) {
    const nodo = document.createElement(etiquetaHtml);
    if (texto !== undefined) { nodo.textContent = texto; }
    if (clase) { nodo.className = clase; }
    return nodo;
  }

  function valorTexto(v) {
    if (v === null || v === undefined) { return '—'; }
    if (typeof v === 'object') { return JSON.stringify(v); }
    return String(v);
  }

  /** Lista de objetos -> tabla. */
  function tablaLista(lista) {
    const columnas = [];
    lista.forEach(function (fila) {
      Object.keys(fila).forEach(function (k) { if (columnas.indexOf(k) === -1) { columnas.push(k); } });
    });
    const tabla = el('table');
    const cabecera = el('tr');
    columnas.forEach(function (c) { cabecera.appendChild(el('th', etiqueta(c))); });
    tabla.appendChild(el('thead')).appendChild(cabecera);
    const cuerpo = el('tbody');
    lista.forEach(function (fila) {
      const tr = el('tr');
      columnas.forEach(function (c) { tr.appendChild(el('td', valorTexto(fila[c]))); });
      cuerpo.appendChild(tr);
    });
    tabla.appendChild(cuerpo);
    const envoltura = el('div', undefined, 'envoltura-tabla');
    envoltura.appendChild(tabla);
    return envoltura;
  }

  /** Objeto -> tabla de dos columnas (campo / valor). */
  function fichaObjeto(objeto) {
    const tabla = el('table');
    const cuerpo = el('tbody');
    Object.keys(objeto).forEach(function (k) {
      const tr = el('tr');
      tr.appendChild(el('td', etiqueta(k), 'clave'));
      tr.appendChild(el('td', valorTexto(objeto[k])));
      cuerpo.appendChild(tr);
    });
    tabla.appendChild(cuerpo);
    const envoltura = el('div', undefined, 'envoltura-tabla');
    envoltura.appendChild(tabla);
    return envoltura;
  }

  function mostrarError(titulo, detalle) {
    const caja = el('div', undefined, 'error-caja');
    caja.appendChild(el('strong', titulo));
    if (detalle) { caja.appendChild(el('p', detalle)); }
    $('contenido').replaceChildren(caja);
  }

  async function iniciar() {
    const ruta = new URLSearchParams(window.location.search).get('url');

    if (!ruta || !ruta.startsWith('/api/') || ruta.startsWith('//')) {
      mostrarError('Ruta no válida', 'Vuelve al panel y elige un endpoint.');
      return;
    }

    $('btn-api').href = ruta;
    $('r-ruta').textContent = ruta;
    $('resumen').hidden = false;
    $('btn-copiar').addEventListener('click', function () {
      const completa = window.location.origin + ruta;
      const boton = $('btn-copiar');
      const aviso = function (t) { boton.textContent = t; setTimeout(function () { boton.textContent = 'Copiar URL'; }, 1500); };
      if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(completa).then(function () { aviso('¡Copiada!'); }, function () { aviso(completa); });
      } else {
        aviso(completa);
      }
    });

    const inicio = performance.now();
    let respuesta;
    try {
      respuesta = await fetch(ruta, { headers: { 'Accept': 'application/json, text/plain, */*' } });
    } catch (e) {
      $('r-estado').textContent = 'Sin conexión';
      $('r-estado').classList.add('estado--error');
      $('r-tiempo').textContent = '—';
      mostrarError('No se pudo conectar con el servidor', 'Comprueba que la aplicación esté corriendo.');
      return;
    }

    $('r-tiempo').textContent = Math.round(performance.now() - inicio) + ' ms';
    $('r-estado').textContent = respuesta.status + ' ' + (respuesta.statusText || '');
    if (!respuesta.ok) { $('r-estado').classList.add('estado--error'); }

    const tipo = respuesta.headers.get('content-type') || '';
    const contenido = $('contenido');

    if (tipo.indexOf('json') !== -1) {
      const datos = await respuesta.json();

      if (!respuesta.ok) {
        const mensaje = datos && (datos.message || datos.error) ? (datos.message || datos.error) : 'La API devolvió un error.';
        mostrarError('La API respondió con error ' + respuesta.status, mensaje);
      } else if (Array.isArray(datos)) {
        if (datos.length === 0) {
          contenido.replaceChildren(el('p', 'No se encontraron resultados con esos filtros.', 'vacio'));
        } else if (typeof datos[0] === 'object' && datos[0] !== null) {
          contenido.replaceChildren(el('p', datos.length + (datos.length === 1 ? ' resultado' : ' resultados')), tablaLista(datos));
        } else {
          contenido.replaceChildren(el('pre', datos.join('\n')));
        }
      } else if (datos !== null && typeof datos === 'object') {
        contenido.replaceChildren(fichaObjeto(datos));
      } else {
        contenido.replaceChildren(el('pre', String(datos)));
      }

      $('json-crudo').textContent = JSON.stringify(datos, null, 2);
      $('detalle-json').hidden = false;
    } else {
      const texto = await respuesta.text();
      if (!respuesta.ok) {
        mostrarError('La API respondió con error ' + respuesta.status, texto.slice(0, 300));
      } else {
        contenido.replaceChildren(el('pre', texto));
      }
    }
  }

  iniciar();
})();
