# 📘 Proyecto Escolar – Aplicaciones Móviles  
## Universidad Tecnológica de México (UNITEC)  
### Proyecto: **QRPhising – Simulador Educativo de Quishing**  
### Equipo: **12**  
### Integrantes:
- **Gilberto Morales Torres**
- **Angel Luis Arreola López**

---

# 🎣 QRPhising — Simulador Educativo de Ataques QR (Quishing)

> Aplicación Android educativa para el análisis y demostración de vectores de ataque mediante códigos QR (*Quishing*).

![Kotlin](https://img.shields.io/badge/kotlin-1.9.0-purple.svg)
![Platform](https://img.shields.io/badge/platform-android-green.svg)
![Gradle](https://img.shields.io/badge/build-gradle-blue.svg)
![Status](https://img.shields.io/badge/status-educational-orange.svg)

---

## ⚠️ Disclaimer (Descargo de Responsabilidad)

**IMPORTANTE:**

Esta aplicación ha sido desarrollada **exclusivamente con fines académicos y de investigación en ciberseguridad**, como parte de la materia de **Aplicaciones Móviles** en la Universidad Tecnológica de México (UNITEC).

Su propósito es demostrar las vulnerabilidades inherentes al escaneo de códigos QR sin validación previa y ayudar a desarrolladores y usuarios a identificar posibles ataques de ingeniería social.

- El autor **no se hace responsable** del mal uso de esta aplicación.
- El uso de este software en sistemas o redes sin autorización explícita es **ilegal**.
- Esta app debe utilizarse únicamente en entornos controlados y con el consentimiento de las partes involucradas.

---

## 📖 Descripción

**QRPhising** es una aplicación nativa de Android escrita en **Kotlin**.  
Permite simular escenarios de **Quishing** (phishing vía códigos QR) para:

- Auditar la seguridad básica de dispositivos móviles.
- Generar conciencia en usuarios finales sobre riesgos al escanear QR.
- Apoyar en actividades educativas y de demostración en ciberseguridad.

---

## ✨ Funcionalidades

- 🔧 **Generación de códigos QR** con payloads personalizados (por ejemplo, URLs).
- 🎣 **Simulación de redirecciones web potencialmente maliciosas** con fines educativos.
- 🌐 **Visualización previa de la URL** antes de abrirla en el navegador.
- 🧩 Interfaz limpia y fácil de usar (basada en **Material Design**).
- 📷 *(Opcional, si tu implementación lo incluye)* **Escáner de QR integrado** con visualización del contenido antes de ejecutar la acción.

---

## 🛠️ Tecnologías y Herramientas

El proyecto está construido utilizando el siguiente stack tecnológico:

- **Lenguaje:** [Kotlin](https://kotlinlang.org/)
- **IDE Recomendado:** Android Studio Ladybug (o superior).
- **Sistema de Construcción:** Gradle.
- **Librerías Clave:**
  - Librería para QR como **ZXing** (o la que se haya usado en el proyecto).
  - **Jetpack Compose** o **XML** para el diseño de la UI (según implementación actual).
- **Plataforma objetivo:** Dispositivos Android.

---

## 🚀 Instalación y Configuración

Para ejecutar este proyecto en tu entorno local, sigue estos pasos:

### ✅ Prerrequisitos

- **Android Studio** instalado.
- **JDK 17** o superior.
- Un dispositivo Android con **modo desarrollador** activo o un **Emulador** configurado.

---

### 1️⃣ Clonar el repositorio

```bash
git clone https://github.com/GilbertoMorales02/QRPhising.git
cd QRPhising