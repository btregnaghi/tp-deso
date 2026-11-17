"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import styles from "./preguntas.module.css";

export default function GestionarPreguntas() {

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

    // Cargar competencias y factores desde la API
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

    // Filtrar los factores según la competencia seleccionada
    const competenciaSeleccionada = competencias.find(
        item => item.nombre === competencia
    );

    const factoresDisponibles = competenciaSeleccionada
        ? factores.filter(
            item => item.competenciaId === competenciaSeleccionada.id
        )
        : factores;

    // Buscar preguntas mediante la API REST
    async function buscarPreguntas(evento) {

        evento.preventDefault();

        setMensaje("");
        setPreguntaSeleccionada(null);
        setPreguntas([]);
        setBusquedaRealizada(false);

        if (!competencia && !factor && !nombre.trim()) {
            setMensaje("Debe ingresar al menos un criterio de búsqueda.");
            return;
        }

        const parametros = new URLSearchParams();

        if (competencia) {
            parametros.append("competencia", competencia);
        }

        if (factor) {
            parametros.append("factor", factor);
        }

        if (nombre.trim()) {
            parametros.append("nombre", nombre.trim());
        }

        setBuscando(true);

        try {

            const respuesta = await fetch(
                `/api/preguntas?${parametros.toString()}`
            );

            if (!respuesta.ok) {
                const error = await respuesta.json();

                throw new Error(
                    error.mensaje || "Error al buscar preguntas."
                );
            }

            const resultado = await respuesta.json();

            setPreguntas(resultado);
            setBusquedaRealizada(true);

            if (resultado.length === 0) {
                setMensaje("No se encontraron preguntas.");
            }

        } catch (error) {
            setMensaje(error.message);
        } finally {
            setBuscando(false);
        }
    }

    return (
        <div className={styles.pagina}>

            <header className={styles.encabezado}>
                <div className={styles.logo}>
                    Capit@l Humano
                    <span>Gestión de evaluaciones</span>
                </div>

                <nav className={styles.navegacion}>
                    <Link href="/">Inicio</Link>
                    <Link href="/preguntas">Preguntas</Link>
                    <span className={styles.consultor}>Consultor</span>
                </nav>
            </header>

            <main className={styles.contenido}>

                <h1>Gestionar preguntas</h1>

                <form
                    className={styles.formulario}
                    onSubmit={buscarPreguntas}
                >

                    <div className={styles.campo}>
                        <label htmlFor="competencia">Competencia</label>

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

                    <div className={styles.campo}>
                        <label htmlFor="factor">Factor</label>

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

                    <div className={styles.botonesBusqueda}>

                        <button type="submit" disabled={buscando}>
                            {buscando ? "Buscando..." : "Buscar"}
                        </button>

                        <button
                            type="button"
                            onClick={() => setMensaje(
                                "CU021 pendiente de implementación."
                            )}
                        >
                            Nuevo
                        </button>

                    </div>
                </form>

                {mensaje && (
                    <p className={styles.mensaje} role="status">
                        {mensaje}
                    </p>
                )}

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
                                        <td>{pregunta.competenciaNombre}</td>
                                        <td>{pregunta.factorNombre}</td>
                                        <td>{pregunta.nombre}</td>

                                        <td>
                                            <input
                                                type="radio"
                                                name="preguntaSeleccionada"
                                                aria-label={`Seleccionar ${pregunta.nombre}`}
                                                checked={
                                                    preguntaSeleccionada === pregunta.id
                                                }
                                                onChange={() =>
                                                    setPreguntaSeleccionada(pregunta.id)
                                                }
                                            />
                                        </td>
                                    </tr>

                                ))}

                            </tbody>

                        </table>

                        <div className={styles.botonesGestion}>

                            <button
                                type="button"
                                disabled={preguntaSeleccionada === null}
                                onClick={() => setMensaje(
                                    "CU023 pendiente de implementación."
                                )}
                            >
                                Eliminar
                            </button>

                            <button
                                type="button"
                                disabled={preguntaSeleccionada === null}
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
        </div>
    );
}