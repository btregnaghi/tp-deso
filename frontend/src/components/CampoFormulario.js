
"use client";

import styles from "./CampoFormulario.module.css";

export default function CampoFormulario({
    id,
    etiqueta,
    tipo = "text",
    valor = "",
    onChange,
    placeholder = "",
    obligatorio = false,
    error = "",
    deshabilitado = false,
    maxLength,
    min,
    max,
    step,
    opciones = [],
    filas = 4
}) {

    const tieneError = Boolean(error);

    const propiedadesComunes = {
        id: id,
        name: id,
        value: valor,
        onChange: onChange,
        disabled: deshabilitado,
        required: obligatorio,
        "aria-invalid": tieneError,
        "aria-describedby": tieneError
            ? `${id}-error`
            : undefined,
        className: `${styles.control} ${
            tieneError ? styles.controlError : ""
        }`
    };

    return (

        <div className={styles.campo}>

            {/* ETIQUETA DEL CAMPO */}

            <label
                htmlFor={id}
                className={styles.etiqueta}
            >

                {etiqueta}
                {obligatorio && "(*)"}

            </label>

            {/* CONTROL DEL FORMULARIO */}

            <div className={styles.contenedorControl}>

                {tipo === "select" ? (

                    <select {...propiedadesComunes}>

                        <option value="">
                            {placeholder || "Seleccione una opción"}
                        </option>

                        {opciones.map((opcion) => (

                            <option
                                key={opcion.valor}
                                value={opcion.valor}
                            >
                                {opcion.etiqueta}
                            </option>

                        ))}

                    </select>

                ) : tipo === "textarea" ? (

                    <textarea
                        {...propiedadesComunes}
                        placeholder={placeholder}
                        maxLength={maxLength}
                        rows={filas}
                    />

                ) : (

                    <input
                        {...propiedadesComunes}
                        type={tipo}
                        placeholder={placeholder}
                        maxLength={maxLength}
                        min={min}
                        max={max}
                        step={step}
                    />

                )}

                {/* ICONO DE ERROR */}

                {tieneError && (

                    <span
                        className={`${styles.iconoError} ${
                            tipo === "select"
                                ? styles.iconoSelect
                                : tipo === "number"
                                    ? styles.iconoNumero
                                    : ""
                        }`}
                        aria-hidden="true"
                    >

                        <svg
                            width="14"
                            height="14"
                            viewBox="0 0 20 20"
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

                    </span>

                )}

            </div>

            {/* MENSAJE DE VALIDACIÓN */}

            {tieneError && (

                <span
                    id={`${id}-error`}
                    className={styles.mensajeError}
                    role="alert"
                >

                    {error}

                </span>

            )}

        </div>

    );
}
