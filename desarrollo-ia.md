# Registro de Trabajo con IA - Taller 03
**Estudiante(s):**
* María José Ledesma Cordoba - ID:000559241
* Miguel Angel Puente Mejia - ID:000559418

**Curso:** Estructuras de Datos y Algoritmos (2026-20)

**Combinación asignada (punto 4):**
* ID ordenados: 000559241, 000559418
* Cadena concatenada: `000559241000559418`
* SHA-256: `03b9ed6438fafd90cf65c9e126251a9b9db4f271ac83464ccc4719dcba4bb860`
* Hash mod 9 = 7 → **WeightedQuickUnion + Insertion sort**

---

# 1. CONTRATOS DE LOS ADTs

## Persona
- **Precondiciones:** `id` no nulo ni vacío; `-90 <= latitud <= 90`; `-180 <= longitud <= 180`.
- **Postcondiciones:** los campos no cambian tras la construcción; `toString()` incluye id, latitud y longitud.
- **Invariantes:** el objeto es inmutable (atributos finales, sin setters).
- **Criterios de aceptación:** crear una persona válida expone sus datos por los getters; la distancia de una persona consigo misma es 0; la distancia es simétrica (`d(p,q) == d(q,p)`).

## Pareja
- **Precondiciones:** las dos personas son distintas (`id1 != id2`).
- **Postcondiciones:** los identificadores quedan en forma canónica (`id1 < id2`); la distancia queda precalculada y es `>= 0`; `compareTo` compara primero por distancia y, en caso de empate, por `id1` y luego por `id2`.
- **Invariantes:** el objeto es inmutable; el orden es total y determinista (no hay dos parejas distintas "iguales" bajo `compareTo`); `Pareja(p,q)` y `Pareja(q,p)` producen la misma pareja lógica.
- **Criterios de aceptación:** `Pareja(p,q)` y `Pareja(q,p)` tienen el mismo `id1` e `id2`; dos parejas con distinta distancia se ordenan por distancia; dos parejas con la misma distancia se ordenan por `id1` y luego `id2`.
- **Decisión de diseño sobre la distancia:** <¿se almacena la distancia o la distancia al cuadrado? Justificar: la raíz cuadrada no cambia el orden, pero sí el tiempo de generación.>

---

# 2. DOCUMENTACIÓN DE COMPONENTES

# Componente 1: WeightedQuickUnion (Union-Find)

## 1. Entender y descomponer
Queremos construir una red conectando personas por orden de cercanía. Para cada pareja, en ese orden, hay que saber si sus miembros ya están en el mismo grupo y, si no lo están, unirlos. Como esto se repite hasta K veces (con K hasta cerca de P = N(N-1)/2) y cada unión hace dos búsquedas de grupo (F = 2K), necesitamos una estructura donde buscar y unir sea barato. Subproblemas: (a) inicializar cada persona como su propio grupo (`parent[i] = i`, `size[i] = 1`, `count = N`), (b) `find(p)`, que sube por los padres hasta la raíz sin compresión de caminos, (c) `union(p,q)`, que halla las raíces y, si son distintas, cuelga el árbol de menor tamaño del de mayor tamaño y baja `count` en 1, (d) `connected(p,q)` como `find(p) == find(q)`, (e) contadores de accesos al arreglo sin doble conteo, (f) misma API que QuickFind y QuickUnion. Hipótesis de solución: dos arreglos `parent[]` y `size[]`; colgar siempre el árbol pequeño del grande evita que los árboles se vuelvan cadenas, porque un nodo solo aumenta su profundidad cuando su grupo se une a otro de tamaño igual o mayor, así que el tamaño de su grupo se duplica como mínimo y la profundidad queda acotada por lg N.

## 2. PRD

# ADT WeightedQuickUnion (Union-Find con unión por tamaño, sin compresión de caminos)

## Requerimientos
- Entradas: `n` entero (constructor); `p`, `q` enteros en `[0, n)` para `union`, `find` y `connected`.
- Salidas: `find(p)` retorna un `int` (la raíz de la componente); `connected(p,q)` retorna `boolean`; `count()` retorna `int`; los contadores `accesosFind` y `accesosUnion` se exponen como `long`.

## Contrato
- Precondiciones: `n >= 1`; `0 <= p, q < n` en todas las operaciones.
- Postcondiciones: tras `union(p,q)`, `connected(p,q) == true`. `count()` baja exactamente en 1 si las raíces eran distintas y no cambia si eran iguales (unión redundante).
- Invariantes: `parent[r] == r` si y solo si `r` es raíz. `size[r]` es el número de nodos del árbol con raíz `r`. `count()` es el número de raíces. La profundidad de cualquier nodo es `<= floor(lg n)`.

## Restricciones de implementación
- Complejidad temporal esperada: `find` y `union` en O(lg n) accesos en el peor caso.
- Complejidad espacial esperada: Θ(n) (dos arreglos de enteros de tamaño `n`).
- Regla de unión: el árbol de menor tamaño cuelga del de mayor tamaño; en caso de empate, el segundo cuelga del primero.
- Conteo de accesos: cada lectura o escritura de `parent[]` o `size[]` cuenta 1. `find` cuenta una lectura de `parent[]` por cada nodo visitado (profundidad + 1). Una `union` efectiva tiene 4 accesos propios (2 lecturas de `size[]`, 1 escritura de `parent[]`, 1 escritura de `size[]`); una `union` redundante tiene 0 accesos propios. `costoTotalUnion = accesosPropiosUnion + accesosFind(p) + accesosFind(q)`. El contador de find es solo desglose y no se suma al total de la fase de uniones.
- Buenas prácticas / patrones exigidos: misma API que QuickFind y QuickUnion (`UnionFind(n)`, `union`, `find`, `connected`, `count`, `resetCounters`).
- Prohibiciones: no usar compresión de caminos (esa variante es aparte, para el punto 6f); no usar bibliotecas externas para la lógica.

## Criterios de aceptación (casos de prueba)
- Caso típico: con `n=4`, la secuencia `union(0,1)`, `union(2,3)`, `union(0,2)` deja `count() == 1` y `connected(1,3) == true`.
- Casos borde: `n=1`; `union(p,p)`; unión repetida (redundante); `connected` reflexiva, simétrica y transitiva; misma partición que QuickFind y QuickUnion para la misma secuencia de uniones.
- Casos de error: índice fuera de rango lanza `IndexOutOfBoundsException` (o la excepción definida por el equipo).

## 3. Prompt(s) utilizado(s)
Herramienta: Claude. Fecha: 28 de septiembre de 2026.
> *"Hazme en Java una clase WeightedQuickUnion con union, find, connected y count, usando parent[] y size[], sin compresión de caminos. Cuenta los accesos a los arreglos: un contador para find y otro para union, y el de union incluye los find internos sin sumarse doble. Sin librerías, con pruebas assert."*

## 4. Registro de validación crítica
- Casos de aceptación ejecutados: <resultado de la secuencia `union(0,1)`, `union(2,3)`, `union(0,2)` con `n=4`>.
- Casos borde probados: <resultado de `n=1`, `union(p,p)`, unión repetida, `connected` reflexiva/simétrica/transitiva, índice fuera de rango, equivalencia de particiones con QuickFind y QuickUnion>.
- Verificación manual de los contadores: con `n=4` y la convención del PRD, la secuencia `union(0,1)`, `union(2,3)`, `union(0,2)`, `union(1,3)` debe dar totales de union de 6, 6, 6 y 5 accesos (la última es redundante: find(1) = 2 accesos, find(3) = 3 accesos, 0 propios), para un acumulado esperado de `accesosUnion = 23` y `accesosFind = 11`. Medido: <...>.
- Análisis de complejidad propio: <deducir del código: costo de `find` en función de la profundidad, costo de `union`, y espacio>.
- Verificación de pre/postcondiciones e invariantes: <comprobar que `count()` coincide con el número de raíces tras cada unión, que `size[r]` coincide con el tamaño real de cada árbol y que la profundidad máxima observada es `<= floor(lg n)`>.

## 5. Problemas encontrados y correcciones
No encontramos errores en la lógica de la IA. Revisamos a mano que find no usara compresión de caminos y que el árbol pequeño colgara del grande, con el segundo colgando del primero en caso de empate. También comprobamos que los accesos se contaran como decía el PRD: profundidad + 1 lecturas en find y 4 accesos propios en una unión efectiva.

## 6. Reflexión final (prueba de propiedad)
Aprendimos que colgar siempre el árbol pequeño del grande mantiene los árboles bajos, y por eso find y union cuestan poco aunque haya muchas uniones. También vimos que contar accesos exige ponerse de acuerdo en qué cuenta como uno, o los números no coinciden con los calculados a mano. Sí podríamos hacerlo sin ayuda: lo clave es revisar las raíces antes de unir y actualizar size solo de la raíz que queda.

# Componente 2: Insertion Sort

## 1. Entender y descomponer
Antes de empezar a unir hay que ordenar de menor a mayor distancia las P = N(N-1)/2 parejas. La ordenación siempre procesa las parejas completas, sin intercalarse con las uniones, para poder medir las fases por separado. Subproblemas: (a) `less(a, b)`, que compara y cuenta una comparación por llamada, (b) `exch(a, i, j)`, que intercambia y cuenta un intercambio, (c) bucle externo sobre `i` desde 1 hasta `n-1`, (d) bucle interno `for (j = i; j > 0 && less(a[j], a[j-1]); j--) exch(a, j, j-1)`, (e) genericidad: el algoritmo solo conoce elementos comparables, sin nada específico de `Pareja`, (f) contadores de `less` y `exch` por separado, con método para reiniciarlos. Punto clave: el tamaño de entrada del ordenamiento es P, no N, así que el resultado hay que expresarlo primero en P y luego sustituir P ≈ N²/2. Hipótesis de solución: insertion sort es adaptativo, su trabajo depende del número de inversiones; esperamos P-1 comparaciones y 0 intercambios si el arreglo ya está ordenado, del orden de P²/2 comparaciones e intercambios si está al revés, y unos P²/4 con datos aleatorios, que es el caso base del taller. En términos de N eso es del orden de N⁴.

## 2. PRD

# Algoritmo Insertion Sort genérico instrumentado

## Requerimientos
- Entradas: arreglo `a` de elementos genéricos `T` comparables.
- Salidas: el mismo arreglo `a` ordenado de forma creciente (in place); contadores `comparaciones` (less) e `intercambios` (exch) expuestos como `long`.

## Contrato
- Precondiciones: `a` no es `null`; los elementos son mutuamente comparables.
- Postcondiciones: `a` es una permutación ordenada del original; el número de intercambios es igual al número de inversiones del arreglo original.
- Invariantes: al inicio de cada iteración `i`, el prefijo `a[0..i-1]` está ordenado y contiene los mismos elementos que el prefijo original.

## Restricciones de implementación
- Complejidad temporal esperada (tamaño de entrada `n`, que en el taller es `P`): mejor caso `n-1` comparaciones y 0 intercambios; peor caso ≈ `n²/2` comparaciones y ≈ `n²/2` intercambios; caso promedio ≈ `n²/4` de cada uno.
- Complejidad espacial esperada: O(1) adicional.
- Buenas prácticas / patrones exigidos: algoritmo genérico (no especializado en `Pareja`); `less` cuenta 1 por cada llamada al comparador y `exch` cuenta 1 por cada intercambio; método para reiniciar los contadores.
- Prohibiciones: no usar el ordenamiento de la biblioteca estándar.

## Criterios de aceptación (casos de prueba)
- Caso típico: un arreglo aleatorio pequeño queda ordenado y es una permutación del original.
- Casos borde: arreglo vacío; un solo elemento; ya ordenado; en orden inverso; con elementos repetidos.
- Casos de error: arreglo `null` lanza `IllegalArgumentException` (o la excepción definida por el equipo).

## 3. Prompt(s) utilizado(s)
Herramienta: Claude. Fecha: 28 de septiembre de 2026.
> *"Hazme en Java un insertion sort genérico (Comparable) con contadores separados de comparaciones e intercambios y un método para reiniciarlos. Sin Arrays.sort y con pruebas assert."*

## 4. Registro de validación crítica
- Casos de aceptación ejecutados: <resultado con un arreglo aleatorio pequeño>.
- Casos borde probados: <resultado de vacío, un elemento, ya ordenado, inverso, repetidos, permutación del original>.
- Verificación manual de los contadores: con 5 enteros, `[1,2,3,4,5]` debe dar 4 comparaciones y 0 intercambios; `[5,4,3,2,1]` debe dar 10 comparaciones y 10 intercambios; `[3,1,4,2,5]` debe dar 6 comparaciones y 3 intercambios (igual al número de inversiones). Medidos: <...>.
- Análisis de complejidad propio: <deducir del código, en función de P y luego de N (P ≈ N²/2), para mejor, peor y promedio>.
- Verificación de pre/postcondiciones e invariantes: <comprobar que tras cada iteración `i` el prefijo `a[0..i]` está ordenado, que los intercambios igualan las inversiones y que los contadores se reinician correctamente>.

## 5. Problemas encontrados y correcciones
La IA hizo bien el ciclo principal, pero no revisó el caso de un arreglo null En vez de lanzar IllegalArgumentException, como decía el PRD, el código lanzaba NullPointerException Fue un olvido del prompt, que no pedía esa validación. Lo arreglamos a mano agregando un if (a == null) throw new IllegalArgumentException al inicio de sort.

## 6. Reflexión final (prueba de propiedad)
Aprendimos que insertion sort trabaja más o menos según qué tan desordenado esté el arreglo, y que aquí el tamaño que importa es P (las parejas), no N (las personas). También vimos que los contadores hay que revisarlos a mano, porque si están mal ubicados el código igual funciona. Sí podríamos hacerlo sin ayuda: lo clave es que el ciclo se detiene cuando el elemento ya no es menor que el anterior o llega al inicio.

# Componente 3: Programa de construcción de la red y medición

## 1. Entender y descomponer
Dado un conjunto de personas con coordenadas, hay que construir la red de forma codiciosa: generar todas las parejas con su distancia, ordenarlas de la más cercana a la más lejana y recorrerlas uniendo cada pareja hasta que todas las personas queden en un solo grupo. Interesa medir cuántas parejas hacen falta (K), cuántas uniones fueron redundantes (Ured = K - (N-1)) y cuál es la distancia de la última unión efectiva (r*, el radio de conexión). Subproblemas: (a) generar o cargar las personas (fuera del cronómetro), (b) fase 2: construir las P parejas con la distancia precalculada y el desempate determinista, (c) fase 3: ordenar todo el arreglo con insertion sort, antes de las uniones, (d) fase 4: recorrer las parejas aplicando `union`, decidiendo si fue efectiva comparando `count()` antes y después (sin usar `connected()`, porque sus `find` internos alterarían los contadores y romperían la relación F = 2K), guardando `(k, C(k))` y parando cuando `count() == 1`, (e) registrar las métricas (`less`, `exch`, K, Ured, F, r*, accesos de find y de union), (f) excluir del cronómetro la generación de personas, la E/S y la graficación. Hipótesis de solución: separar las fases permite ver qué domina. Esperamos que el ordenamiento (cuadrático en P, del orden de N⁴) domine ampliamente a la fase de uniones (K uniones de costo O(lg N)), de modo que medir solo el total ocultaría el comportamiento de las uniones; esperamos también `Uef = N-1` siempre y una curva de componentes contra uniones efectivas que sea una recta de pendiente -1.

## 2. PRD

# Programa de construcción de la red y medición de fases (WeightedQuickUnion + Insertion sort)

## Requerimientos
- Entradas: `N` entero (número de personas), `semilla` entera, distribución (`uniforme` o `cumulos`), o un archivo CSV con encabezado `id,latitud,longitud`.
- Salidas: tiempos por fase (generación de parejas, ordenamiento, uniones); `less` y `exch`; `K`, `Ured` y `F = 2K`; `r*` en grados y en km; accesos de find y de union; lista de tuplas `(k, C(k))`.

## Contrato
- Precondiciones: `N >= 2`; se generan las `P = N(N-1)/2` parejas `(i < j)`.
- Postcondiciones: `count() == 1` al terminar; `Uef == N-1`; `Ured == K - Uef`; `F == 2K`; `r*` es la distancia de la última pareja efectiva.
- Invariantes: la ordenación termina completa antes de empezar las uniones; la efectividad de una unión se decide comparando `count()` antes y después de la llamada.

## Restricciones de implementación
- Complejidad temporal esperada: generación de parejas Θ(P); ordenamiento con insertion sort ≈ P²/4 comparaciones en promedio; uniones: a lo sumo K uniones, cada una O(lg N) en accesos.
- Complejidad espacial esperada: Θ(P) por el arreglo de parejas.
- Buenas prácticas / patrones exigidos: un cronómetro por fase (`time.perf_counter()`, `System.nanoTime()` o `Stopwatch`); `resetCounters()` antes de cada medición; semillas fijas y repeticiones.
- Prohibiciones: no dejar E/S, generación de personas ni llamadas a `connected()` dentro de los bloques cronometrados.

## Criterios de aceptación (casos de prueba)
- Caso típico: con `N=25` y semilla fija, `Uef = 24`, `count() == 1` y `C(k)` no creciente.
- Casos borde: `N=2` (P=1, K=1, Ured=0); puntos colineales; distancias repetidas (el desempate hace el orden reproducible); misma semilla dos veces da los mismos `K`, `r*`, `less`, `exch` y accesos.
- Casos de error: `N < 2` lanza `IllegalArgumentException` (o la excepción definida por el equipo).

## 3. Prompt(s) utilizado(s)
Herramienta: Claude. Fecha: 28 de septiembre de 2026.
> *"Con mis clases Persona, Pareja, WeightedQuickUnion e InsertionSort, haz en Java el programa que arma las parejas, las ordena y las une hasta quedar un solo grupo, decidiendo si la unión fue efectiva con count() antes y después, sin connected(). Mide cada fase por separado con System.nanoTime(), sin incluir la generación de datos ni la E/S, y muestra K, uniones redundantes, r* y los contadores."*

## 4. Registro de validación crítica
- Casos de aceptación ejecutados: <resultado con `N=25` y semilla fija: `Uef`, `count()` final y forma de `C(k)`>.
- Casos borde probados: <resultado de `N=2`, puntos colineales, distancias repetidas y reproducibilidad con la misma semilla>.
- Verificación manual de los contadores: <comprobar `F = 2K`, `Uef = N-1`, `Ured = K - Uef`, que `accesosUnion` incluye los accesos de los `find` internos sin sumar `accesosFind`, y qué líneas quedan dentro y fuera de cada cronómetro>.
- Análisis de complejidad propio: <deducir del código el costo de cada fase>.
- Verificación de pre/postcondiciones e invariantes: <comprobar que la ordenación termina antes de las uniones y que el ciclo se detiene cuando `count() == 1`>.

## 5. Problemas encontrados y correcciones
<Qué falló, si fue error de la IA o ambigüedad del PRD, y cómo se corrigió.>

## 6. Reflexión final (prueba de propiedad)
<3 a 5 líneas del equipo.>
