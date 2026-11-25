
"use client";

import { useEffect, useRef, useId } from "react";
import styles from "./VentanaModal.module.css";

export default function VentanaModal({
    abierta,
    tipo = "advertencia",
    titulo,
    mensaje,
    botones = [],
    onCerrar,
    cerrarConCualquierTecla = false,
    bloqueada = false
}) {

    const dialogoRef = useRef(null);
    const idTitulo = useId();
    const idMensaje = useId();

    // ==========================================
    // CONFIGURACIÓN SEGÚN EL TIPO DE VENTANA
    // ==========================================

    const configuraciones = {

        error: {
            titulo: "Error",
            cabecera: styles.cabeceraError,
            icono: styles.iconoError
        },

        advertencia: {
            titulo: "Advertencia",
            cabecera: styles.cabeceraAdvertencia,
            icono: styles.iconoAdvertencia
        },

        exito: {
            titulo: "Operación Exitosa",
            cabecera: styles.cabeceraExito,
            icono: styles.iconoExito
        }

    };

    const configuracion =
        configuraciones[tipo] || configuraciones.advertencia;

    // ==========================================
    // APERTURA Y CIERRE DEL DIÁLOGO
    // ==========================================

    useEffect(() => {

        const dialogo = dialogoRef.current;

        if (!dialogo) {
            return;
        }

        if (abierta && !dialogo.open) {

            dialogo.showModal();

        } else if (!abierta && dialogo.open) {

            dialogo.close();

        }

    }, [abierta]);

    // ==========================================
    // FUNCIONES DE CIERRE
    // ==========================================

    function cerrarVentana() {

        if (!bloqueada) {
            onCerrar?.();
        }
    }

    function manejarCancelacion(evento) {

        evento.preventDefault();

        cerrarVentana();
    }

    function manejarTeclado(evento) {

        if (
            abierta &&
            cerrarConCualquierTecla &&
            !bloqueada &&
            !evento.repeat
        ) {

            evento.preventDefault();

            cerrarVentana();

        }
    }

    // ==========================================
    // INTERFAZ DE LA VENTANA
    // ==========================================

    return (

        <dialog
            ref={dialogoRef}
            className={styles.modal}
            aria-labelledby={idTitulo}
            aria-describedby={idMensaje}
            onCancel={manejarCancelacion}
            onKeyDown={manejarTeclado}
        >

            {/* CABECERA */}

            <div className={configuracion.cabecera}>

                <span
                    className={styles.decoracionVentana}
                    aria-hidden="true"
                >
                    ● ● ●
                </span>

                <h2 id={idTitulo}>

                    {titulo || configuracion.titulo}

                </h2>

            </div>

            {/* CONTENIDO */}

            <div className={styles.cuerpoModal}>

                {/* ICONO */}

                <svg
                    className={configuracion.icono}
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

                    {tipo === "exito" ? (

                        <>
                            <circle
                                cx="12"
                                cy="12"
                                r="10"
                            />

                            <path d="m8 12 3 3 5-6" />
                        </>

                    ) : (

                        <>
                            <path d="M12 3 2 21h20L12 3Z" />
                            <path d="M12 9v5" />
                            <path d="M12 17h.01" />
                        </>

                    )}

                </svg>

                {/* MENSAJE */}

                <p id={idMensaje}>

                    {mensaje}

                </p>

                {/* ACCIONES */}

                {cerrarConCualquierTecla ? (

                    <button
                        type="button"
                        className={styles.indicacionModal}
                        onClick={cerrarVentana}
                        disabled={bloqueada}
                        autoFocus
                    >

                        Presione cualquier tecla para regresar.

                    </button>

                ) : (

                    <div className={styles.botonesModal}>

                        {botones.length > 0 ? (

                            botones.map((boton, indice) => (

                                <button
                                    key={indice}
                                    type="button"
                                    className={
                                        boton.principal
                                            ? styles.botonPrincipal
                                            : ""
                                    }
                                    onClick={boton.onClick}
                                    disabled={
                                        bloqueada ||
                                        boton.disabled
                                    }
                                    autoFocus={indice === 0}
                                >

                                    {boton.texto}

                                </button>

                            ))

                        ) : (

                            <button
                                type="button"
                                onClick={cerrarVentana}
                                disabled={bloqueada}
                                autoFocus
                            >

                                Aceptar

                            </button>

                        )}

                    </div>

                )}

            </div>

        </dialog>

    );
}
