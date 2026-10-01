import random
import csv
import argparse

CENTRO = (6.25, -75.57)   # Medellín
LADO_KM = 10
KM_POR_GRADO = 111.19
MEDIO_LADO_GRADOS = (LADO_KM / 2) / KM_POR_GRADO  # ≈ 0.045°

def grados_a_km(grados):
    return grados * KM_POR_GRADO

def generar_personas(n, semilla, distribucion):
    random.seed(semilla)
    personas = []

    if distribucion == "uniforme":
        for i in range(n):
            lat = random.uniform(CENTRO[0] - MEDIO_LADO_GRADOS, CENTRO[0] + MEDIO_LADO_GRADOS)
            lon = random.uniform(CENTRO[1] - MEDIO_LADO_GRADOS, CENTRO[1] + MEDIO_LADO_GRADOS)
            personas.append({"id": i, "lat": lat, "lon": lon})

    elif distribucion == "cumulos":
        # 3 cúmulos separados, pero dentro de la misma región de 10 km
        centros = [
            (CENTRO[0] - 0.02, CENTRO[1] - 0.02),
            (CENTRO[0] + 0.02, CENTRO[1] + 0.01),
            (CENTRO[0] + 0.00, CENTRO[1] + 0.025),
        ]
        sigma = 0.008  # grados, bien menor que el tamaño de la región

        for i in range(n):
            cx, cy = random.choice(centros)
            lat = random.gauss(cx, sigma)
            lon = random.gauss(cy, sigma)
            lat = max(CENTRO[0] - MEDIO_LADO_GRADOS, min(CENTRO[0] + MEDIO_LADO_GRADOS, lat))
            lon = max(CENTRO[1] - MEDIO_LADO_GRADOS, min(CENTRO[1] + MEDIO_LADO_GRADOS, lon))
            personas.append({"id": i, "lat": lat, "lon": lon})

    return personas


def guardar_csv(personas, nombre_archivo):
    with open(nombre_archivo, "w", newline="", encoding="utf-8") as archivo:
        escritor = csv.writer(archivo)

        escritor.writerow(["id", "lat", "lon"])

        for persona in personas:
            escritor.writerow([
                persona["id"],
                persona["lat"],
                persona["lon"]
            ])


def main():
    parser = argparse.ArgumentParser(
        description="Generador de personas con coordenadas geográficas"
    )

    parser.add_argument("-n", type=int, required=True,
                        help="Cantidad de personas")
    parser.add_argument("-s", type=int, required=True,
                        help="Semilla aleatoria")
    parser.add_argument("-d", choices=["uniforme", "cumulos"], required=True,
                        help="Distribución de las coordenadas")
    parser.add_argument("-o", type=str, required=True,
                        help="Nombre del archivo CSV de salida")

    args = parser.parse_args()

    personas = generar_personas(args.n, args.s, args.d)
    guardar_csv(personas, args.o)


if __name__ == "__main__":
    main()