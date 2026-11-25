"use client";

import { useState } from "react";
import Link from "next/link";

import VentanaModal from "../../components/VentanaModal";
import CampoFormulario from "../../components/CampoFormulario";

import styles from "./evaluaciones.module.css";

export default function EvaluacionesPage() {

    // ==========================================
    // CRITERIOS DE BÚSQUEDA
    // ==========================================

    const [apellido, setApellido] = useState("");
    const [nombre, setNombre] = useState("");
    const [nroCandidato, setNroCandidato] = useState("");

    // ==========================================
    // ERRORES DE VALIDACIÓN
    // ==========================================

    const [errores, setErrores] = useState({
        apellido: "",
        nombre: "",
        nroCandidato: "",
        general: ""
    });

    // ==========================================
    // RESULTADOS DE BÚSQUEDA
    // ==========================================

    const [candidatos, setCandidatos] = useState([]);
    const [busquedaRealizada, setBusquedaRealizada] = useState(false);
    const [buscando, setBuscando] = useState(false);

    // ==========================================
    // VALIDACIÓN DE CANDIDATOS
    // ==========================================

    const [validandoCandidatos, setValidandoCandidatos] =
        useState(false);

    // ==========================================
    // CANDIDATOS A EVALUAR
    // ==========================================

    const [candidatosAEvaluar, setCandidatosAEvaluar] =
        useState([]);

    const [seleccionEncontrados, setSeleccionEncontrados] =
        useState([]);

    const [seleccionAEvaluar, setSeleccionAEvaluar] =
        useState([]);

    // ==========================================
    // CONTROL DE PANTALLAS
    // ==========================================

    const [etapa, setEtapa] = useState(1);

    // ==========================================
    // SELECCIÓN DE FUNCIÓN LABORAL
    // ==========================================

    const [puestos, setPuestos] = useState([]);
    const [codigoPuesto, setCodigoPuesto] = useState("");
    const [detallePuesto, setDetallePuesto] = useState(null);

    const [cargandoDetallePuesto, setCargandoDetallePuesto] =
        useState(false);

    // ==========================================
    // CANDIDATOS CON CLAVES DE INGRESO
    // ==========================================

    const [candidatosConClaves, setCandidatosConClaves] =
        useState([]);

    // ==========================================
    // VENTANAS DE MENSAJES
    // ==========================================

    const [ventana, setVentana] = useState(null);

    function mostrarMensaje(mensaje, tipo = "error") {
        setVentana({
            tipo,
            mensaje
        });
    }

    function cerrarMensaje() {
        setVentana(null);
    }

    // ==========================================
    // VALIDACIÓN DE LOS FILTROS
    // ==========================================

    function validarFiltros() {

        const nuevosErrores = {
            apellido: "",
            nombre: "",
            nroCandidato: "",
            general: ""
        };

        const apellidoLimpio = apellido.trim();
        const nombreLimpio = nombre.trim();
        const numeroLimpio = nroCandidato.trim();

        // Debe existir al menos un criterio.

        if (
            !apellidoLimpio &&
            !nombreLimpio &&
            !numeroLimpio
        ) {
            nuevosErrores.general =
                "Debe ingresar al menos un criterio de búsqueda.";
        }

        // Validar longitud del apellido.

        if (apellidoLimpio.length > 50) {
            nuevosErrores.apellido =
                "El apellido no puede superar los 50 caracteres.";
        }

        // Validar longitud del nombre.

        if (nombreLimpio.length > 50) {
            nuevosErrores.nombre =
                "El nombre no puede superar los 50 caracteres.";
        }

        // Validar número de candidato.

        if (numeroLimpio) {

            if (!/^\d{1,10}$/.test(numeroLimpio)) {

                nuevosErrores.nroCandidato =
                    "Ingrese un número entero de hasta 10 dígitos.";

            } else if (Number(numeroLimpio) <= 0) {

                nuevosErrores.nroCandidato =
                    "El número debe ser mayor que cero.";

            } else if (Number(numeroLimpio) > 2147483647) {

                nuevosErrores.nroCandidato =
                    "El número ingresado supera el máximo permitido.";

            }
        }

        setErrores(nuevosErrores);

        return Object.values(nuevosErrores).every(
            (error) => error === ""
        );
    }

    // ==========================================
    // LIMPIAR ERRORES AL MODIFICAR UN CAMPO
    // ==========================================

    function limpiarErrorCampo(campo) {

        setErrores((anteriores) => ({
            ...anteriores,
            [campo]: "",
            general: ""
        }));

    }

    // ==========================================
    // BÚSQUEDA DE CANDIDATOS
    // ==========================================

    async function buscarCandidatos(evento) {

        evento.preventDefault();

        if (validandoCandidatos || buscando) {
            return;
        }

        if (!validarFiltros()) {
            return;
        }

        const parametros = new URLSearchParams();

        if (apellido.trim()) {
            parametros.set("apellido", apellido.trim());
        }

        if (nombre.trim()) {
            parametros.set("nombre", nombre.trim());
        }

        if (nroCandidato.trim()) {
            parametros.set(
                "nroCandidato",
                nroCandidato.trim()
            );
        }

        setBuscando(true);
        setSeleccionEncontrados([]);
        setBusquedaRealizada(false);
        setCandidatos([]);

        try {

            const respuesta = await fetch(
                `/api/candidatos?${parametros.toString()}`,
                {
                    cache: "no-store"
                }
            );

            if (!respuesta.ok) {

                const error = await respuesta
                    .json()
                    .catch(() => ({}));

                throw new Error(
                    error.mensaje ||
                    "No se pudo realizar la búsqueda de candidatos."
                );
            }

            const datos = await respuesta.json();

            if (!Array.isArray(datos)) {
                throw new Error(
                    "La respuesta de candidatos no es válida."
                );
            }

            setCandidatos(datos);
            setBusquedaRealizada(true);

        } catch (error) {

            mostrarMensaje(
                error.message ||
                "Ocurrió un error al consultar el servidor.",
                "error"
            );

        } finally {

            // Restablecer el estado independientemente
            // del resultado de la búsqueda.

            setBuscando(false);

        }
    }

    // ==========================================
    // SELECCIÓN INDIVIDUAL
    // ==========================================

    function seleccionarEncontrado(id) {

        setSeleccionEncontrados((anterior) =>
            anterior.includes(id)
                ? anterior.filter((item) => item !== id)
                : [...anterior, id]
        );
    }

    function seleccionarAEvaluar(id) {

        setSeleccionAEvaluar((anterior) =>
            anterior.includes(id)
                ? anterior.filter((item) => item !== id)
                : [...anterior, id]
        );
    }

    // ==========================================
    // SELECCIÓN MÚLTIPLE
    // ==========================================

    function seleccionarTodosEncontrados(evento) {

        if (evento.target.checked) {

            setSeleccionEncontrados(
                candidatos.map((candidato) => candidato.id)
            );

        } else {

            setSeleccionEncontrados([]);

        }
    }

    function seleccionarTodosAEvaluar(evento) {

        if (evento.target.checked) {

            setSeleccionAEvaluar(
                candidatosAEvaluar.map(
                    (candidato) => candidato.id
                )
            );

        } else {

            setSeleccionAEvaluar([]);

        }
    }

    // ==========================================
    // AGREGAR CANDIDATOS
    // ==========================================

    function agregarCandidatos() {

        if (validandoCandidatos || buscando) {
            return;
        }

        if (seleccionEncontrados.length === 0) {

            mostrarMensaje(
                "¡Seleccionar candidatos para agregar!",
                "error"
            );

            return;
        }

        const seleccionados = candidatos.filter(
            (candidato) =>
                seleccionEncontrados.includes(candidato.id)
        );

        setCandidatosAEvaluar((anteriores) => {

            // Evitar candidatos duplicados.

            const nuevos = seleccionados.filter(
                (candidato) =>
                    !anteriores.some(
                        (existente) =>
                            existente.id === candidato.id
                    )
            );

            return [...anteriores, ...nuevos];

        });

        setSeleccionEncontrados([]);
    }

    // ==========================================
    // QUITAR CANDIDATOS
    // ==========================================

    function quitarCandidatos() {

        if (validandoCandidatos) {
            return;
        }

        if (seleccionAEvaluar.length === 0) {

            mostrarMensaje(
                "¡Seleccionar candidatos para quitar!",
                "error"
            );

            return;
        }

        setCandidatosAEvaluar((anteriores) =>
            anteriores.filter(
                (candidato) =>
                    !seleccionAEvaluar.includes(candidato.id)
            )
        );

        setSeleccionAEvaluar([]);
    }

    // ==========================================
    // VALIDACIÓN DE CANDIDATOS A EVALUAR
    // ==========================================

    async function validarCandidatos() {

        if (validandoCandidatos || buscando) {
            return;
        }

        // Verificar que existan candidatos a evaluar.

        if (candidatosAEvaluar.length === 0) {

            mostrarMensaje(
                "¡Seleccionar candidatos para evaluar!",
                "error"
            );

            return;
        }

        // Obtener los identificadores de los candidatos.

        const idsCandidatos = candidatosAEvaluar.map(
            (candidato) => candidato.id
        );

        setValidandoCandidatos(true);

        try {

            // Solicitar la validación al backend.

            const respuesta = await fetch(
                "/api/cuestionarios/validar-candidatos",
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({
                        idsCandidatos
                    }),

                    cache: "no-store"
                }
            );

            if (!respuesta.ok) {

                const error = await respuesta
                    .json()
                    .catch(() => ({}));

                throw new Error(
                    error.mensaje ||
                    "No se pudo validar a los candidatos."
                );
            }

            const datos = await respuesta.json();

            // Verificar si existen candidatos bloqueados.

            if (datos.puedeAvanzar === false) {

                const mensaje = datos.candidatosBloqueados
                    .map(
                        (candidato) =>
                            `¡El candidato "${candidato.apellido} ${candidato.nombre} ${candidato.nroCandidato}" posee cuestionarios activos o en proceso y no puede ser evaluado nuevamente!`
                    )
                    .join("\n\n");

                mostrarMensaje(
                    mensaje || "Existen candidatos bloqueados.",
                    "error"
                );

                return;
            }

            // Verificar que la respuesta sea válida.

            if (datos.puedeAvanzar !== true) {

                throw new Error(
                    "La respuesta de validación no es válida."
                );
            }

            // ==========================================
            // RECUPERAR LOS PUESTOS DISPONIBLES
            // ==========================================

            const respuestaPuestos = await fetch(
                "/api/puestos",
                {
                    cache: "no-store"
                }
            );

            if (!respuestaPuestos.ok) {

                const error = await respuestaPuestos
                    .json()
                    .catch(() => ({}));

                throw new Error(
                    error.mensaje ||
                    "No se pudieron recuperar las funciones laborales."
                );
            }

            const datosPuestos =
                await respuestaPuestos.json();

            if (!Array.isArray(datosPuestos)) {

                throw new Error(
                    "La respuesta de puestos no es válida."
                );
            }

            // Guardar los puestos recuperados.

            setPuestos(datosPuestos);

            // Inicializar los datos de la segunda pantalla.

            setCodigoPuesto("");
            setDetallePuesto(null);
            setCandidatosConClaves([]);

            // Avanzar hacia la segunda pantalla.

            setEtapa(2);

        } catch (error) {

            mostrarMensaje(
                error.message ||
                "Ocurrió un error al consultar el servidor.",
                "error"
            );

        } finally {

            setValidandoCandidatos(false);

        }
    }

    // ==========================================
    // SELECCIÓN DE FUNCIÓN LABORAL
    // ==========================================

    async function seleccionarPuesto(evento) {

        if (cargandoDetallePuesto) {
            return;
        }

        const codigo = evento.target.value;

        setCodigoPuesto(codigo);

        // Limpiar la información del puesto anterior.

        setDetallePuesto(null);

        // Invalidar cualquier preparación anterior.

        setCandidatosConClaves([]);

        // Si no se seleccionó ningún puesto,
        // no es necesario consultar el backend.

        if (!codigo) {
            return;
        }

        setCargandoDetallePuesto(true);

        try {

            const respuesta = await fetch(
                `/api/puestos/${encodeURIComponent(codigo)}`,
                {
                    cache: "no-store"
                }
            );

            if (!respuesta.ok) {

                const error = await respuesta
                    .json()
                    .catch(() => ({}));

                throw new Error(
                    error.mensaje ||
                    "No se pudo recuperar el detalle de la función laboral."
                );
            }

            const datos = await respuesta.json();

            // Verificar la estructura de la respuesta.

            if (
                !datos ||
                datos.codigo !== codigo ||
                typeof datos.empresa !== "string" ||
                !Array.isArray(datos.competencias)
            ) {

                throw new Error(
                    "La respuesta del detalle del puesto no es válida."
                );
            }

            // Almacenar la información recuperada.

            setDetallePuesto(datos);

        } catch (error) {

            // Restablecer la selección si ocurrió un error.

            setCodigoPuesto("");
            setDetallePuesto(null);
            setCandidatosConClaves([]);

            mostrarMensaje(
                error.message ||
                "Ocurrió un error al consultar el servidor.",
                "error"
            );

        } finally {

            setCargandoDetallePuesto(false);

        }
    }

    // ==========================================
    // VALIDACIÓN DE LA FUNCIÓN LABORAL
    // ==========================================

    function validarPuesto() {

        if (cargandoDetallePuesto) {
            return;
        }

        // Verificar que se haya seleccionado una función.

        if (!codigoPuesto || !detallePuesto) {

            mostrarMensaje(
                "¡Seleccionar función laboral!",
                "error"
            );

            return;
        }

        // Verificar que existan competencias asociadas.

        if (detallePuesto.competencias.length === 0) {

            mostrarMensaje(
                "La función laboral seleccionada no posee competencias asociadas.",
                "error"
            );

            return;
        }

        // ==========================================
        // IMPLEMENTACIÓN PENDIENTE
        // ==========================================

        // En la siguiente etapa consultaremos al backend
        // para verificar las competencias evaluables
        // y preparar las claves de ingreso.

        mostrarMensaje(
            "La validación de competencias y la generación de claves todavía no se encuentran implementadas.",
            "advertencia"
        );
    }

    // ==========================================
    // INTERFAZ GRÁFICA
    // ==========================================

    return (

        <main className={styles.contenedor}>

            {/* ENCABEZADO COMÚN */}

            <header className={styles.encabezado}>

                <div>
                    <h1>Capit@l Humano</h1>
                    <p>Gestión de evaluaciones</p>
                </div>

                <nav>

                    <Link href="/">Inicio</Link>

                    <Link href="/preguntas">
                        Preguntas
                    </Link>

                    <span>Consultor</span>

                </nav>

            </header>

            {/* ======================================== */}
            {/* PRIMERA PANTALLA: BÚSQUEDA DE CANDIDATOS */}
            {/* ======================================== */}

            {etapa === 1 ? (

                <section className={styles.contenido}>

                    <h2>Búsqueda de candidatos</h2>

                    {/* FORMULARIO DE BÚSQUEDA */}

                    <form
                        className={styles.filtros}
                        onSubmit={buscarCandidatos}
                        noValidate
                    >

                        <CampoFormulario
                            id="apellido"
                            etiqueta="Apellido"
                            valor={apellido}
                            placeholder="Ingrese apellido"
                            error={errores.apellido}
                            onChange={(evento) => {
                                setApellido(evento.target.value);
                                limpiarErrorCampo("apellido");
                            }}
                        />

                        <CampoFormulario
                            id="nombre"
                            etiqueta="Nombre"
                            valor={nombre}
                            placeholder="Ingrese nombre"
                            error={errores.nombre}
                            onChange={(evento) => {
                                setNombre(evento.target.value);
                                limpiarErrorCampo("nombre");
                            }}
                        />

                        <CampoFormulario
                            id="nroCandidato"
                            etiqueta="Número de candidato"
                            tipo="text"
                            valor={nroCandidato}
                            placeholder="Ingrese número"
                            error={errores.nroCandidato}
                            onChange={(evento) => {
                                setNroCandidato(evento.target.value);
                                limpiarErrorCampo("nroCandidato");
                            }}
                        />

                        {/* CORRECCIÓN PF06:
                            Mantener fijo el texto del botón. */}

                        <button
                            type="submit"
                            disabled={
                                buscando || validandoCandidatos
                            }
                        >
                            Buscar
                        </button>

                        {/* ERROR GENERAL DEL FORMULARIO */}

                        {errores.general && (

                            <div
                                className={styles.errorFormulario}
                                role="alert"
                            >

                                <svg
                                    width="14"
                                    height="14"
                                    viewBox="0 0 20 20"
                                    aria-hidden="true"
                                >

                                    <circle
                                        cx="10"
                                        cy="10"
                                        r="9"
                                        fill="currentColor"
                                    />

                                    <path
                                        d="M10 5.5v5"
                                        stroke="white"
                                        strokeWidth="2"
                                        strokeLinecap="round"
                                    />

                                    <circle
                                        cx="10"
                                        cy="14"
                                        r="1"
                                        fill="white"
                                    />

                                </svg>

                                <span>
                                    {errores.general}
                                </span>

                            </div>

                        )}

                    </form>

                    {/* GRILLAS DE CANDIDATOS */}

                    <div className={styles.grillas}>

                        {/* GRILLA IZQUIERDA */}

                        <section className={styles.grilla}>

                            <h3>Candidatos encontrados</h3>

                            <table>

                                <thead>

                                    <tr>

                                        <th>Apellido</th>
                                        <th>Nombre</th>
                                        <th>Número</th>

                                        <th>

                                            <input
                                                type="checkbox"
                                                checked={
                                                    candidatos.length > 0 &&
                                                    seleccionEncontrados.length ===
                                                    candidatos.length
                                                }
                                                onChange={
                                                    seleccionarTodosEncontrados
                                                }
                                                disabled={
                                                    candidatos.length === 0 ||
                                                    validandoCandidatos ||
                                                    buscando
                                                }
                                                aria-label="Seleccionar todos los candidatos encontrados"
                                            />

                                        </th>

                                    </tr>

                                </thead>

                                <tbody>

                                    {candidatos.map((candidato) => (

                                        <tr key={candidato.id}>

                                            <td>
                                                {candidato.apellido}
                                            </td>

                                            <td>
                                                {candidato.nombre}
                                            </td>

                                            <td>
                                                {candidato.nroCandidato}
                                            </td>

                                            <td>

                                                <input
                                                    type="checkbox"
                                                    checked={
                                                        seleccionEncontrados.includes(
                                                            candidato.id
                                                        )
                                                    }
                                                    onChange={() =>
                                                        seleccionarEncontrado(
                                                            candidato.id
                                                        )
                                                    }
                                                    disabled={
                                                        validandoCandidatos ||
                                                        buscando
                                                    }
                                                    aria-label={
                                                        `Seleccionar ${candidato.nombre} ${candidato.apellido}`
                                                    }
                                                />

                                            </td>

                                        </tr>

                                    ))}

                                </tbody>

                            </table>

                            {/* MENSAJE DE BÚSQUEDA SIN RESULTADOS */}

                            {busquedaRealizada &&
                                candidatos.length === 0 && (

                                    <p role="status">
                                        No se encontraron candidatos.
                                    </p>

                            )}

                            <button
                                type="button"
                                onClick={agregarCandidatos}
                                disabled={
                                    buscando || validandoCandidatos
                                }
                            >
                                Agregar
                            </button>

                        </section>

                        {/* GRILLA DERECHA */}

                        <section className={styles.grilla}>

                            <h3>Candidatos a evaluar</h3>

                            <table>

                                <thead>

                                    <tr>

                                        <th>Apellido</th>
                                        <th>Nombre</th>
                                        <th>Número</th>

                                        <th>

                                            <input
                                                type="checkbox"
                                                checked={
                                                    candidatosAEvaluar.length > 0 &&
                                                    seleccionAEvaluar.length ===
                                                    candidatosAEvaluar.length
                                                }
                                                onChange={
                                                    seleccionarTodosAEvaluar
                                                }
                                                disabled={
                                                    candidatosAEvaluar.length === 0 ||
                                                    validandoCandidatos
                                                }
                                                aria-label="Seleccionar todos los candidatos a evaluar"
                                            />

                                        </th>

                                    </tr>

                                </thead>

                                <tbody>

                                    {candidatosAEvaluar.map((candidato) => (

                                        <tr key={candidato.id}>

                                            <td>
                                                {candidato.apellido}
                                            </td>

                                            <td>
                                                {candidato.nombre}
                                            </td>

                                            <td>
                                                {candidato.nroCandidato}
                                            </td>

                                            <td>

                                                <input
                                                    type="checkbox"
                                                    checked={
                                                        seleccionAEvaluar.includes(
                                                            candidato.id
                                                        )
                                                    }
                                                    onChange={() =>
                                                        seleccionarAEvaluar(
                                                            candidato.id
                                                        )
                                                    }
                                                    disabled={
                                                        validandoCandidatos
                                                    }
                                                    aria-label={
                                                        `Quitar ${candidato.nombre} ${candidato.apellido}`
                                                    }
                                                />

                                            </td>

                                        </tr>

                                    ))}

                                </tbody>

                            </table>

                            <button
                                type="button"
                                onClick={quitarCandidatos}
                                disabled={validandoCandidatos}
                            >
                                Quitar
                            </button>

                        </section>

                    </div>

                    {/* BOTÓN SIGUIENTE */}

                    <div className={styles.acciones}>

                        <button
                            type="button"
                            onClick={validarCandidatos}
                            disabled={
                                buscando || validandoCandidatos
                            }
                        >
                            Siguiente
                        </button>

                    </div>

                </section>

            ) : (

                /* ====================================== */
                /* SEGUNDA PANTALLA: EVALUACIÓN           */
                /* ====================================== */

                <section className={styles.contenido}>

                    <h2>Evaluación de candidatos</h2>

                    {/* DATOS DE LA FUNCIÓN LABORAL */}

                    <div className={styles.datosPuesto}>

                        {/* CORRECCIÓN PF28:
                            CampoFormulario genera la opción inicial.
                            Solo enviamos los puestos recuperados. */}

                        <CampoFormulario
                            id="codigoPuesto"
                            etiqueta="Función laboral"
                            tipo="select"
                            valor={codigoPuesto}
                            placeholder="Seleccionar puesto"
                            opciones={puestos.map((puesto) => ({
                                valor: puesto.codigo,
                                etiqueta: puesto.nombre
                            }))}
                            onChange={seleccionarPuesto}
                            deshabilitado={cargandoDetallePuesto}
                        />

                        <CampoFormulario
                            id="empresa"
                            etiqueta="Empresa"
                            valor={detallePuesto?.empresa || ""}
                            deshabilitado={true}
                        />

                    </div>

                    {/* ESTADO DE LA CONSULTA */}

                    {cargandoDetallePuesto && (

                        <p
                            className={styles.mensajeEstado}
                            role="status"
                        >
                            Recuperando información de la función laboral...
                        </p>

                    )}

                    {puestos.length === 0 && (

                        <p
                            className={styles.mensajeEstado}
                            role="status"
                        >
                            No hay funciones laborales registradas.
                        </p>

                    )}

                    {/* GRILLA DE COMPETENCIAS */}

                    <section className={styles.grillaCompetencias}>

                        <h3>Competencias</h3>

                        <table>

                            <thead>

                                <tr>
                                    <th>Competencia</th>
                                    <th>Ponderación</th>
                                </tr>

                            </thead>

                            <tbody>

                                {detallePuesto?.competencias.map(
                                    (competencia) => (

                                        <tr key={competencia.id}>

                                            <td>
                                                {competencia.nombre}
                                            </td>

                                            <td>
                                                {competencia.ponderacion}
                                            </td>

                                        </tr>

                                    )
                                )}

                            </tbody>

                        </table>

                        {detallePuesto &&
                            detallePuesto.competencias.length === 0 && (

                                <p
                                    className={styles.mensajeEstado}
                                    role="status"
                                >
                                    La función laboral no posee competencias asociadas.
                                </p>

                        )}

                    </section>

                    {/* SIGUIENTE: VALIDAR FUNCIÓN LABORAL */}

                    <div className={styles.accionesPuesto}>

                        <button
                            type="button"
                            onClick={validarPuesto}
                            disabled={cargandoDetallePuesto}
                        >
                            Siguiente
                        </button>

                    </div>

                    {/* ====================================== */}
                    {/* GRILLA INFERIOR: CANDIDATOS Y CLAVES    */}
                    {/* ====================================== */}

                    <section className={styles.grillaClaves}>

                        <h3>Candidatos a evaluar</h3>

                        <table>

                            <thead>

                                <tr>
                                    <th>Apellido</th>
                                    <th>Nombre</th>
                                    <th>Tipo de documento</th>
                                    <th>Número de documento</th>
                                    <th>Clave de ingreso</th>
                                </tr>

                            </thead>

                            <tbody>

                                {candidatosConClaves.map(
                                    (candidato) => (

                                        <tr key={candidato.id}>

                                            <td>
                                                {candidato.apellido}
                                            </td>

                                            <td>
                                                {candidato.nombre}
                                            </td>

                                            <td>
                                                {candidato.tipoDoc}
                                            </td>

                                            <td>
                                                {candidato.nroDoc}
                                            </td>

                                            <td>
                                                {candidato.claveIngreso}
                                            </td>

                                        </tr>

                                    )
                                )}

                            </tbody>

                        </table>

                    </section>

                    {/* BOTONES DE FINALIZACIÓN */}

                    <div className={styles.accionesEtapa}>

                        <Link
                            href="/"
                            className={styles.botonCancelar}
                        >
                            Cancelar
                        </Link>

                        <button
                            type="button"
                            disabled
                        >
                            Finalizar
                        </button>

                    </div>

                </section>

            )}

            {/* COMPONENTE REUTILIZABLE DE MENSAJES */}

            <VentanaModal
                abierta={ventana !== null}
                tipo={ventana?.tipo}
                mensaje={ventana?.mensaje}
                onCerrar={cerrarMensaje}
            />

        </main>

    );
}