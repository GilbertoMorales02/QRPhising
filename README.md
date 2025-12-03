# QRPhising 🎣📱

> **Aplicación Android educativa para el análisis y demostración de vectores de ataque mediante códigos QR (Quishing).**

![Kotlin](https://img.shields.io/badge/kotlin-1.9.0-purple.svg)
![Platform](https://img.shields.io/badge/platform-android-green.svg)
![Gradle](https://img.shields.io/badge/build-gradle-blue.svg)
![Status](https://img.shields.io/badge/status-educational-orange.svg)

## ⚠️ Disclaimer (Descargo de Responsabilidad)

**IMPORTANTE:**

Esta aplicación ha sido desarrollada **exclusivamente con fines académicos y de investigación en ciberseguridad**. Su propósito es demostrar las vulnerabilidades inherentes al escaneo de códigos QR sin validación previa y ayudar a desarrolladores y usuarios a identificar posibles ataques de ingeniería social.

* El autor **no se hace responsable** del mal uso de esta aplicación.
* El uso de este software en sistemas o redes sin autorización explícita es ilegal.

---

## 📖 Descripción

**QRPhising** es una aplicación nativa de Android escrita en Kotlin. Permite simular escenarios de *Quishing* (Phishing vía QR) para auditar la seguridad de dispositivos móviles y la concienciación del usuario.

### Funcionalidades
* Generación de Códigos QR con payloads personalizados.
* Simulación de redirecciones web maliciosas.
* Interfaz limpia y fácil de usar (Material Design).
* *(Opcional: agrega aquí si tu app también escanea)* Escáner de QR integrado con visualización de URL antes de abrir.

## 🛠️ Tecnologías y Herramientas

El proyecto está construido utilizando el siguiente stack tecnológico:

* **Lenguaje:** [Kotlin](https://kotlinlang.org/)
* **IDE Recomendado:** Android Studio Ladybug (o superior).
* **Sistema de Construcción:** Gradle.
* **Librerías Clave:**
    * *ZXing* (o la librería que hayas usado para QR).
    * *Jetpack Compose* o *XML* (según tu UI).

## 🚀 Instalación y Configuración

Para ejecutar este proyecto en tu entorno local, sigue estos pasos:

### Prerrequisitos
* Tener instalado **Android Studio**.
* JDK 17 o superior.
* Un dispositivo Android con modo desarrollador activo o un Emulador.

### Pasos

1.  **Clona el repositorio:**
    ```bash
    git clone [https://github.com/GilbertoMorales02/QRPhising.git](https://github.com/GilbertoMorales02/QRPhising.git)
    cd QRPhising
    ```

2.  **Abre el proyecto en Android Studio:**
    * Abre Android Studio, selecciona `File > Open` y busca la carpeta clonada.
    * Espera a que Gradle sincronice las dependencias (`Sync Project with Gradle Files`).

3.  **Compilación (Build):**
    Si prefieres usar la terminal para generar el APK:
    ```bash
    # En Windows
    gradlew assembleDebug
