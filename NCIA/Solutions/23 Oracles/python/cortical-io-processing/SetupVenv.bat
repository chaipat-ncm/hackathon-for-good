call pip install virtualenv
call virtualenv "%~dp0\venv"
call "%~dp0\venv\Scripts\activate"
call python -m pip install --upgrade pip
call pip install retinasdk
