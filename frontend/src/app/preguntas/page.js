
"use client";

import { useState, useEffect, useRef } from "react";
import Link from "next/link";
import styles from "./preguntas.module.css";

export default function GestionarPreguntas() {

    // ==========================================
    // Estados correspondientes al CU020
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
    // Estados correspondientes al CU023
    // ==========================================

    const [preguntaAEliminar, setPreguntaAEliminar] = useState(null);
    const [eliminando, setEliminando] = useState(false);
    const [mensajeEliminacion, setMensajeEliminacion] = useState("");

    const dialogoConfirmacion = useRef(null);
    const dialogoExito = useRef(null);

    // ==========================================
    // Cargar competencias y factores
    // ==========================================

    useEffect(() => {

        async function cargarDatos() {

            try {

                const [respuestaCompetencias, respuestaFactores] =
                    await Promise.all([
                        fetch("/api/competencias"),
                        fetch("/api/factores")
                    ]);

                if (!respuestaCompetencias.ok || !respuestaFactores.ok) {
                    throw new Error("No se pudieron cargar los filtros.");
                }

                setCompetencias(await respuestaCompetencias.json());
                setFactores(await respuestaFactores.json());

            } catch (error) {

                setMensaje(error.message);

            }
        }

        cargarDatos();

    }, []);

    // ==========================================
    // Controlar la ventana de advertencia
    // ==========================================

    useEffect(() => {

        const dialogo = dialogoConfirmacion.current;

        if (!dialogo) {
            return;
        }

        if (preguntaAEliminar && !dialogo.open) {

            dialogo.showModal();

        } else if (!preguntaAEliminar && dialogo.open) {

            dialogo.close();

        }

    }, [preguntaAEliminar]);

    // ==========================================
    // Controlar la ventana de operación exitosa
    // ==========================================

    useEffect(() => {

        const dialogo = dialogoExito.current;

        if (!dialogo) {
            return;
        }

        if (mensajeEliminacion && !dialogo.open) {

            dialogo.showModal();
            dialogo.focus();

        } else if (!mensajeEliminacion && dialogo.open) {

            dialogo.close();

        }

    }, [mensajeEliminacion]);

    // ==========================================
    // Filtrar factores según la competencia
    // ==========================================

    const competenciaSeleccionada = competencias.find(
        item => item.nombre === competencia
    );

    const factoresDisponibles = competenciaSeleccionada
        ? factores.filter(
            item => item.competenciaId === competenciaSeleccionada.id
        )
        : factores;

    // ==========================================
    // CU020 - Gestionar preguntas
    // ==========================================

    async function ejecutarBusqueda(criterios) {

        setMensaje("");
        setPreguntaSeleccionada(null);
        setPreguntas([]);
        setBusquedaRealizada(false);

        if (
            !criterios.competencia &&
            !criterios.factor &&
            !criterios.nombre.trim()
        ) {

            setMensaje("Debe ingresar al menos un criterio de búsqueda.");
            return;

        }

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

                const error = await respuesta.json().catch(() => ({}));

                throw new Error(
                    error.mensaje || "Error al buscar preguntas."
                );

            }

            const resultado = await respuesta.json();

            setPreguntas(resultado);
            setBusquedaRealizada(true);
            setUltimosCriterios(criterios);

            if (resultado.length === 0) {

                setMensaje("No se encontraron preguntas.");

            }

        } catch (error) {

            setMensaje(error.message);

        } finally {

            setBuscando(false);

        }
    }

    // Buscar preguntas desde el formulario

    async function buscarPreguntas(evento) {

        evento.preventDefault();

        await ejecutarBusqueda({
            competencia,
            factor,
            nombre
        });

    }

    // ==========================================
    // CU023 - Dar de baja una pregunta
    // ==========================================

    // Solicitar la eliminación

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

    // Cancelar la eliminación

    function cancelarEliminacion() {

        if (eliminando) {

            return;

        }

        setPreguntaAEliminar(null);

    }

    // Confirmar la eliminación

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

                const error = await respuesta.json().catch(() => ({}));

                throw new Error(
                    error.mensaje || "Error al eliminar la pregunta."
                );

            }

            setMensajeEliminacion(
                `Los datos de la pregunta "${preguntaAEliminar.nombre}" han sido eliminados del sistema.`
            );

            setPreguntaAEliminar(null);
            setPreguntaSeleccionada(null);

        } catch (error) {

            setMensaje(error.message);
            setPreguntaAEliminar(null);

        } finally {

            setEliminando(false);

        }
    }

    // Finalizar el CU023 y actualizar la grilla

    async function finalizarEliminacion() {

        if (!mensajeEliminacion) {

            return;

        }

        setMensajeEliminacion("");

        if (ultimosCriterios) {

            setCompetencia(ultimosCriterios.competencia);
            setFactor(ultimosCriterios.factor);
            setNombre(ultimosCriterios.nombre);

            await ejecutarBusqueda(ultimosCriterios);

        }
    }

    // ==========================================
    // Interfaz gráfica
    // ==========================================

    return (

        <div className={styles.pagina}>

            {/* Encabezado */}

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

            {/* Contenido principal */}

            <main className={styles.contenido}>

                <h1>Gestionar preguntas</h1>

                {/* Formulario de búsqueda */}

                <form
                    className={styles.formulario}
                    onSubmit={buscarPreguntas}
                >

                    {/* Competencia */}

                    <div className={styles.campo}>

                        <label htmlFor="competencia">
                            Competencia
                        </label>

                        <select
                            id="competencia"
                            value={competencia}
                            onChange={e => {

                                setCompetencia(e.target.value);
                                setFactor("");

                            }}
                        >

                            <option value="">
                                Seleccione una competencia
                            </option>

                            {competencias.map(item => (

                                <option
                                    key={item.id}
                                    value={item.nombre}
                                >
                                    {item.nombre}
                                </option>

                            ))}

                        </select>

                    </div>

                    {/* Nombre de pregunta */}

                    <div className={styles.campo}>

                        <label htmlFor="nombre">
                            Nombre de pregunta
                        </label>

                        <input
                            id="nombre"
                            type="text"
                            placeholder="Ingrese nombre de la pregunta"
                            value={nombre}
                            onChange={e => setNombre(e.target.value)}
                        />

                    </div>

                    {/* Factor */}

                    <div className={styles.campo}>

                        <label htmlFor="factor">
                            Factor
                        </label>

                        <select
                            id="factor"
                            value={factor}
                            onChange={e => setFactor(e.target.value)}
                        >

                            <option value="">
                                Seleccione un factor
                            </option>

                            {factoresDisponibles.map(item => (

                                <option
                                    key={item.id}
                                    value={item.nombre}
                                >
                                    {item.nombre}
                                </option>

                            ))}

                        </select>

                    </div>

                    {/* Botones del formulario */}

                    <div className={styles.botonesBusqueda}>

                        <button
                            type="submit"
                            disabled={buscando}
                        >

                            {buscando ? "Buscando..." : "Buscar"}

                        </button>

                        <button
                            type="button"
                            disabled={buscando}
                            onClick={() => setMensaje(
                                "CU021 pendiente de implementación."
                            )}
                        >

                            Nuevo

                        </button>

                    </div>

                </form>

                {/* Mensajes generales */}

                {mensaje && (

                    <p
                        className={styles.mensaje}
                        role="status"
                    >

                        {mensaje}

                    </p>

                )}

                {/* Grilla de resultados */}

                {busquedaRealizada && preguntas.length > 0 && (

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
                                                aria-label={`Seleccionar ${pregunta.nombre}`}
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

                        {/* Botones de gestión */}

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

            {/* =====================================
                CU023 - Ventana de advertencia
                ===================================== */}

            <dialog
                ref={dialogoConfirmacion}
                className={`${styles.modal} ${styles.modalAdvertencia}`}
                aria-labelledby="tituloConfirmacion"
                aria-describedby="mensajeConfirmacion"
                onCancel={evento => {

                    evento.preventDefault();
                    cancelarEliminacion();

                }}
            >

                {preguntaAEliminar && (

                    <>

                        {/* Cabecera amarilla */}

                        <div className={styles.cabeceraAdvertencia}>

                            <span
                                className={styles.decoracionVentana}
                                aria-hidden="true"
                            >
                                ● ● ●
                            </span>

                            <h2 id="tituloConfirmacion">
                                Advertencia
                            </h2>

                        </div>

                        {/* Contenido de la advertencia */}

                        <div className={styles.cuerpoModal}>

                            <svg
                                className={styles.iconoAdvertencia}
                                width="40"
                                height="40"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                strokeWidth="1.8"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                aria-hidden="true"
                            >

                                <path d="M12 3 2 21h20L12 3Z" />
                                <path d="M12 9v5" />
                                <path d="M12 17h.01" />

                            </svg>

                            <p id="mensajeConfirmacion">

                                {`Los datos de la pregunta "${preguntaAEliminar.nombre}" serán eliminados del sistema.`}

                            </p>

                            <div className={styles.botonesModal}>

                                <button
                                    type="button"
                                    disabled={eliminando}
                                    onClick={cancelarEliminacion}
                                    autoFocus
                                >

                                    Cancelar

                                </button>

                                <button
                                    type="button"
                                    className={styles.botonEliminar}
                                    disabled={eliminando}
                                    onClick={confirmarEliminacion}
                                >

                                    {eliminando
                                        ? "Eliminando..."
                                        : "Eliminar"
                                    }

                                </button>

                            </div>

                        </div>

                    </>

                )}

            </dialog>

            {/* =====================================
                CU023 - Ventana de operación exitosa
                ===================================== */}

            <dialog
                ref={dialogoExito}
                className={`${styles.modal} ${styles.modalExito}`}
                aria-labelledby="tituloExito"
                aria-describedby="mensajeExito"
                tabIndex={-1}
                onCancel={evento => {

                    evento.preventDefault();

                }}
                onKeyDown={evento => {

                    if (mensajeEliminacion && !evento.repeat) {

                        evento.preventDefault();
                        finalizarEliminacion();

                    }

                }}
            >

                {/* Cabecera verde */}

                <div className={styles.cabeceraExito}>

                    <span
                        className={styles.decoracionVentana}
                        aria-hidden="true"
                    >
                        ● ● ●
                    </span>

                    <h2 id="tituloExito">
                        Operación Exitosa
                    </h2>

                </div>

                {/* Contenido de la operación exitosa */}

                <div className={styles.cuerpoModal}>

                    <svg
                        className={styles.iconoExito}
                        width="40"
                        height="40"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="1.8"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        aria-hidden="true"
                    >

                        <circle cx="12" cy="12" r="10" />
                        <path d="m8 12 3 3 5-6" />

                    </svg>

                    <p id="mensajeExito">

                        {mensajeEliminacion}

                    </p>

                    <button
                        type="button"
                        className={styles.indicacionModal}
                        onClick={finalizarEliminacion}
                    >

                        Presione cualquier tecla para regresar.

                    </button>

                </div>

            </dialog>

        </div>

    );

}
