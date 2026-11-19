
"use client";

import { useState, useEffect } from "react";
import Link from "next/link";

import CampoFormulario from "../../components/CampoFormulario";
import VentanaModal from "../../components/VentanaModal";

import styles from "./preguntas.module.css";

export default function GestionarPreguntas() {

    // ==========================================
    // CU020 - ESTADOS DE BÚSQUEDA
    // ==========================================

    const [competencias, setCompetencias] = useState([]);
    const [factores, setFactores] = useState([]);

    const [competencia, setCompetencia] = useState("");
    const [factor, setFactor] = useState("");
    const [nombre, setNombre] = useState("");

    const [preguntas, setPreguntas] = useState([]);
    const [busquedaRealizada, setBusquedaRealizada] = useState(false);
    const [preguntaSeleccionada, setPreguntaSeleccionada] = useState(null);
    const [mensaje, setMensaje] = useState("");
    const [buscando, setBuscando] = useState(false);

    const [ultimosCriterios, setUltimosCriterios] = useState(null);

    // ==========================================
    // ERRORES DE VALIDACIÓN
    // ==========================================

    const [errores, setErrores] = useState({
        nombre: "",
        general: ""
    });

    // ==========================================
    // CU023 - ESTADOS DE ELIMINACIÓN
    // ==========================================

    const [preguntaAEliminar, setPreguntaAEliminar] = useState(null);
    const [eliminando, setEliminando] = useState(false);
    const [mensajeEliminacion, setMensajeEliminacion] = useState("");

    // ==========================================
    // VENTANAS DE ERROR
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
    // CARGAR COMPETENCIAS Y FACTORES
    // ==========================================

    useEffect(() => {

        async function cargarDatos() {

            try {

                const [respuestaCompetencias, respuestaFactores] =
                    await Promise.all([
                        fetch("/api/competencias"),
                        fetch("/api/factores")
                    ]);

                if (
                    !respuestaCompetencias.ok ||
                    !respuestaFactores.ok
                ) {

                    throw new Error(
                        "No se pudieron cargar los filtros."
                    );

                }

                setCompetencias(
                    await respuestaCompetencias.json()
                );

                setFactores(
                    await respuestaFactores.json()
                );

            } catch (error) {

                setVentana({
                    tipo: "error",
                    mensaje: error.message
                });

            }

        }

        cargarDatos();

    }, []);

    // ==========================================
    // FILTRAR FACTORES SEGÚN LA COMPETENCIA
    // ==========================================

    const competenciaSeleccionada = competencias.find(
        item => item.nombre === competencia
    );

    const factoresDisponibles = competenciaSeleccionada
        ? factores.filter(
            item =>
                item.competenciaId === competenciaSeleccionada.id
        )
        : factores;

    // ==========================================
    // VALIDAR FILTROS DE BÚSQUEDA
    // ==========================================

    function validarFiltros(criterios) {

        const nuevosErrores = {
            nombre: "",
            general: ""
        };

        if (
            !criterios.competencia &&
            !criterios.factor &&
            !criterios.nombre.trim()
        ) {

            nuevosErrores.general =
                "Debe ingresar al menos un criterio de búsqueda.";

        }

        if (criterios.nombre.trim().length > 50) {

            nuevosErrores.nombre =
                "El nombre de pregunta no puede superar los 50 caracteres.";

        }

        setErrores(nuevosErrores);

        return Object.values(nuevosErrores).every(
            error => error === ""
        );

    }

    // ==========================================
    // LIMPIAR ERRORES DEL FORMULARIO
    // ==========================================

    function limpiarErrorCampo(campo) {

        setErrores(anteriores => ({
            ...anteriores,
            ...(campo === "nombre" ? { nombre: "" } : {}),
            general: ""
        }));

    }

    // ==========================================
    // CU020 - EJECUTAR BÚSQUEDA
    // ==========================================

    async function ejecutarBusqueda(criterios) {

        if (!validarFiltros(criterios)) {

            return;

        }

        setMensaje("");
        setPreguntaSeleccionada(null);
        setPreguntas([]);
        setBusquedaRealizada(false);

        const parametros = new URLSearchParams();

        if (criterios.competencia) {

            parametros.append(
                "competencia",
                criterios.competencia
            );

        }

        if (criterios.factor) {

            parametros.append(
                "factor",
                criterios.factor
            );

        }

        if (criterios.nombre.trim()) {

            parametros.append(
                "nombre",
                criterios.nombre.trim()
            );

        }

        setBuscando(true);

        try {

            const respuesta = await fetch(
                `/api/preguntas?${parametros.toString()}`,
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
                    "Error al buscar preguntas."
                );

            }

            const resultado = await respuesta.json();

            setPreguntas(resultado);
            setBusquedaRealizada(true);
            setUltimosCriterios(criterios);

            if (resultado.length === 0) {

                setMensaje(
                    "No se encontraron preguntas."
                );

            }

        } catch (error) {

            mostrarMensaje(
                error.message ||
                "No se pudo realizar la búsqueda.",
                "error"
            );

        } finally {

            setBuscando(false);

        }

    }

    // ==========================================
    // BUSCAR PREGUNTAS DESDE EL FORMULARIO
    // ==========================================

    async function buscarPreguntas(evento) {

        evento.preventDefault();

        if (buscando || eliminando) {

            return;

        }

        await ejecutarBusqueda({
            competencia,
            factor,
            nombre
        });

    }

    // ==========================================
    // CU023 - SOLICITAR ELIMINACIÓN
    // ==========================================

    function solicitarEliminacion() {

        const pregunta = preguntas.find(
            item => item.id === preguntaSeleccionada
        );

        if (!pregunta) {

            return;

        }

        setMensaje("");
        setPreguntaAEliminar(pregunta);

    }

    // ==========================================
    // CU023 - CANCELAR ELIMINACIÓN
    // ==========================================

    function cancelarEliminacion() {

        if (eliminando) {

            return;

        }

        setPreguntaAEliminar(null);

    }

    // ==========================================
    // CU023 - CONFIRMAR ELIMINACIÓN
    // ==========================================

    async function confirmarEliminacion() {

        if (!preguntaAEliminar || eliminando) {

            return;

        }

        setEliminando(true);
        setMensaje("");

        try {

            const respuesta = await fetch(
                `/api/preguntas/${preguntaAEliminar.id}`,
                {
                    method: "DELETE"
                }
            );

            if (!respuesta.ok) {

                const error = await respuesta
                    .json()
                    .catch(() => ({}));

                throw new Error(
                    error.mensaje ||
                    "Error al eliminar la pregunta."
                );

            }

            setMensajeEliminacion(
                `Los datos de la pregunta "${preguntaAEliminar.nombre}" han sido eliminados del sistema.`
            );

            setPreguntaAEliminar(null);
            setPreguntaSeleccionada(null);

        } catch (error) {

            setPreguntaAEliminar(null);

            mostrarMensaje(
                error.message ||
                "No se pudo eliminar la pregunta.",
                "error"
            );

        } finally {

            setEliminando(false);

        }

    }

    // ==========================================
    // CU023 - FINALIZAR Y ACTUALIZAR GRILLA
    // ==========================================

    async function finalizarEliminacion() {

        if (!mensajeEliminacion) {

            return;

        }

        setMensajeEliminacion("");

        if (ultimosCriterios) {

            setCompetencia(
                ultimosCriterios.competencia
            );

            setFactor(
                ultimosCriterios.factor
            );

            setNombre(
                ultimosCriterios.nombre
            );

            await ejecutarBusqueda(
                ultimosCriterios
            );

        }

    }

    // ==========================================
    // INTERFAZ GRÁFICA
    // ==========================================

    return (

        <div className={styles.pagina}>

            {/* ENCABEZADO */}

            <header className={styles.encabezado}>

                <div className={styles.logo}>

                    Capit@l Humano

                    <span>Gestión de evaluaciones</span>

                </div>

                <nav className={styles.navegacion}>

                    <Link href="/">Inicio</Link>

                    <Link href="/preguntas">Preguntas</Link>

                    <span className={styles.consultor}>

                        Consultor

                    </span>

                </nav>

            </header>

            {/* CONTENIDO PRINCIPAL */}

            <main className={styles.contenido}>

                <h1>Gestionar preguntas</h1>

                {/* FORMULARIO DE BÚSQUEDA */}

                <form
                    className={styles.formulario}
                    onSubmit={buscarPreguntas}
                    noValidate
                >

                    {/* COMPETENCIA */}

                    <div className={styles.campoFiltro}>

                        <CampoFormulario
                            id="competencia"
                            etiqueta="Competencia"
                            tipo="select"
                            valor={competencia}
                            placeholder="Seleccione una competencia"
                            deshabilitado={buscando}
                            opciones={competencias.map(item => ({
                                valor: item.nombre,
                                etiqueta: item.nombre
                            }))}
                            onChange={evento => {

                                setCompetencia(
                                    evento.target.value
                                );

                                setFactor("");

                                limpiarErrorCampo(
                                    "competencia"
                                );

                            }}
                        />

                    </div>

                    {/* NOMBRE DE PREGUNTA */}

                    <div className={styles.campoFiltro}>

                        <CampoFormulario
                            id="nombre"
                            etiqueta="Nombre de pregunta"
                            valor={nombre}
                            placeholder="Ingrese nombre de la pregunta"
                            error={errores.nombre}
                            deshabilitado={buscando}
                            onChange={evento => {

                                setNombre(
                                    evento.target.value
                                );

                                limpiarErrorCampo(
                                    "nombre"
                                );

                            }}
                        />

                    </div>

                    {/* FACTOR */}

                    <div className={styles.campoFiltro}>

                        <CampoFormulario
                            id="factor"
                            etiqueta="Factor"
                            tipo="select"
                            valor={factor}
                            placeholder="Seleccione un factor"
                            deshabilitado={buscando}
                            opciones={factoresDisponibles.map(item => ({
                                valor: item.nombre,
                                etiqueta: item.nombre
                            }))}
                            onChange={evento => {

                                setFactor(
                                    evento.target.value
                                );

                                limpiarErrorCampo(
                                    "factor"
                                );

                            }}
                        />

                    </div>

                    {/* BOTONES DE BÚSQUEDA */}

                    <div className={styles.botonesBusqueda}>

                        <button
                            type="submit"
                            disabled={buscando || eliminando}
                        >

                            {buscando
                                ? "Buscando..."
                                : "Buscar"}

                        </button>

                        <button
                            type="button"
                            disabled={buscando || eliminando}
                            onClick={() => setMensaje(
                                "CU021 pendiente de implementación."
                            )}
                        >

                            Nuevo

                        </button>

                    </div>

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

                {/* MENSAJES INFORMATIVOS */}

                {mensaje && (

                    <p
                        className={styles.mensaje}
                        role="status"
                    >

                        {mensaje}

                    </p>

                )}

                {/* GRILLA DE RESULTADOS */}

                {busquedaRealizada &&
                    preguntas.length > 0 && (

                    <section className={styles.resultados}>

                        <h2>Resultado de búsqueda</h2>

                        <table className={styles.tabla}>

                            <thead>

                                <tr>

                                    <th>Competencia</th>

                                    <th>Factor</th>

                                    <th>Pregunta</th>

                                    <th aria-label="Selección"></th>

                                </tr>

                            </thead>

                            <tbody>

                                {preguntas.map(pregunta => (

                                    <tr
                                        key={pregunta.id}
                                        className={
                                            preguntaSeleccionada === pregunta.id
                                                ? styles.filaSeleccionada
                                                : ""
                                        }
                                    >

                                        <td>

                                            {pregunta.competenciaNombre}

                                        </td>

                                        <td>

                                            {pregunta.factorNombre}

                                        </td>

                                        <td>

                                            {pregunta.nombre}

                                        </td>

                                        <td>

                                            <input
                                                type="radio"
                                                name="preguntaSeleccionada"
                                                aria-label={
                                                    `Seleccionar ${pregunta.nombre}`
                                                }
                                                checked={
                                                    preguntaSeleccionada === pregunta.id
                                                }
                                                onChange={() =>
                                                    setPreguntaSeleccionada(
                                                        pregunta.id
                                                    )
                                                }
                                            />

                                        </td>

                                    </tr>

                                ))}

                            </tbody>

                        </table>

                        {/* BOTONES DE GESTIÓN */}

                        <div className={styles.botonesGestion}>

                            <button
                                type="button"
                                disabled={
                                    preguntaSeleccionada === null ||
                                    eliminando ||
                                    buscando
                                }
                                onClick={solicitarEliminacion}
                            >

                                Eliminar

                            </button>

                            <button
                                type="button"
                                disabled={
                                    preguntaSeleccionada === null ||
                                    eliminando ||
                                    buscando
                                }
                                onClick={() => setMensaje(
                                    "CU022 pendiente de implementación."
                                )}
                            >

                                Modificar

                            </button>

                        </div>

                    </section>

                )}

            </main>

            {/* ==========================================
                VENTANA DE ADVERTENCIA - CU023
                ========================================== */}

            <VentanaModal
                abierta={preguntaAEliminar !== null}
                tipo="advertencia"
                mensaje={
                    preguntaAEliminar
                        ? `Los datos de la pregunta "${preguntaAEliminar.nombre}" serán eliminados del sistema.`
                        : ""
                }
                bloqueada={eliminando}
                onCerrar={cancelarEliminacion}
                botones={[
                    {
                        texto: "Cancelar",
                        onClick: cancelarEliminacion
                    },
                    {
                        texto: eliminando
                            ? "Eliminando..."
                            : "Eliminar",
                        onClick: confirmarEliminacion,
                        principal: true
                    }
                ]}
            />

            {/* ==========================================
                VENTANA DE OPERACIÓN EXITOSA - CU023
                ========================================== */}

            <VentanaModal
                abierta={mensajeEliminacion !== ""}
                tipo="exito"
                mensaje={mensajeEliminacion}
                cerrarConCualquierTecla={true}
                onCerrar={finalizarEliminacion}
            />

            {/* ==========================================
                VENTANA GENERAL DE ERRORES
                ========================================== */}

            <VentanaModal
                abierta={ventana !== null}
                tipo={ventana?.tipo}
                mensaje={ventana?.mensaje}
                onCerrar={cerrarMensaje}
            />

        </div>

    );

}
