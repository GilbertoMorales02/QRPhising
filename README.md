# QRPhising 🎣📱

> **Herramienta educativa para la generación y prueba de vectores de ataque mediante códigos QR (Quishing).**

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Python](https://img.shields.io/badge/python-3.x-yellow.svg)
![Status](https://img.shields.io/badge/status-educational-orange.svg)

## ⚠️ Disclaimer (Descargo de Responsabilidad)

**LEA ESTO ANTES DE USAR:**

Esta herramienta ha sido creada **exclusivamente con fines educativos y de investigación**. El objetivo es ayudar a los profesionales de seguridad y administradores de sistemas a entender cómo funcionan los ataques de *QRLJacking* o *Quishing* para poder defenderse mejor contra ellos.

* El autor **no se hace responsable** del mal uso que se le pueda dar a esta herramienta.
* El uso de este software para atacar objetivos sin el consentimiento previo y mutuo es **ilegal**.
* Es responsabilidad del usuario final obedecer todas las leyes locales, estatales y federales aplicables.

---

## 📖 Descripción

**QRPhising** es una herramienta ligera diseñada para simular ataques de phishing a través de códigos QR. Permite a los investigadores de seguridad generar códigos QR maliciosos (de prueba) que redirigen a portales cautivos o páginas de aterrizaje controladas para evaluar la concienciación de seguridad de los usuarios.

### Características Principales
* Generación rápida de códigos QR a partir de URLs personalizadas.
* Personalización de colores y tamaños del QR.
* *(Opcional - agrega esto si tu herramienta lo tiene)*: Servidor local integrado para capturar credenciales de prueba.
* *(Opcional)*: Inserción de logos dentro del código QR para mayor realismo.

## 🛠️ Requisitos Previos

Asegúrate de tener instalado lo siguiente en tu sistema:

* [Python 3.x](https://www.python.org/downloads/)
* Git

## 🚀 Instalación

Sigue estos pasos para configurar el proyecto en tu entorno local:

1.  **Clona el repositorio:**
    ```bash
    git clone [https://github.com/GilbertoMorales02/QRPhising.git](https://github.com/GilbertoMorales02/QRPhising.git)
    cd QRPhising
    ```

2.  **Instala las dependencias:**
    ```bash
    pip install -r requirements.txt
    ```

## 💻 Uso

Para iniciar la herramienta, ejecuta el script principal:

```bash
python main.py
