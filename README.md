# Aura Quirochema — proyecto Android nuevo

Proyecto Android nativo en Kotlin, sin API de IA ni servidor propio.

## Qué incluye
- Biblioteca local en `app/src/main/assets/library.json`.
- Buscador local de colores, emociones, síntomas, conceptos del Curso y oraciones.
- Contenidos consultables sin conexión.
- Separación explícita entre interpretaciones simbólicas y hechos médicos.
- Oraciones originales de perdón y reflexión.

## Abrir
1. Descarga e instala Android Studio en un ordenador.
2. Abre esta carpeta como proyecto.
3. Espera a que Gradle sincronice.
4. Ejecuta `app`.

## Importante sobre las actualizaciones
Esta primera entrega es una base funcional offline. La actualización automática desde GitHub requiere publicar el catálogo JSON en un repositorio público y añadir una tarea de descarga periódica. No está activada en esta versión porque no se ha proporcionado un repositorio de contenidos ni se puede inventar una URL. No requiere API de IA ni servidor propio.

## Ampliación de contenidos
Edita `app/src/main/assets/library.json`. Mantén las claves JSON existentes. Añade nuevos elementos dentro de `colors`, `emotions`, `symptoms`, `ucdm` o `prayers`.

## Límites de contenido
- Las asociaciones de aura/chakras son interpretaciones espirituales y varían según escuelas.
- La biodescodificación no es una explicación médica demostrada. No utilizar para diagnosticar ni sustituir atención sanitaria.
- El contenido sobre Un Curso de Milagros son síntesis y reflexiones originales, no reproducción de sus 365 lecciones ni de texto protegido.
