"""Aplicacion Flask para servir el frontend de Tombers.
Se delega la logica de negocio en el backend REST desarrollado con Spring Boot."""

from flask import Flask, render_template

app = Flask(__name__)


@app.route("/")
def index():
    """Renderiza la pagina de inicio con los formularios de autenticacion."""
    return render_template("index.html")


@app.route("/feed")
def feed():
    """Renderiza el panel principal con el listado de proyectos."""
    return render_template("feed.html")


@app.route("/bio")
def bio():
    """Renderiza la vista de perfil del usuario autenticado."""
    return render_template("Bio.html")


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)
