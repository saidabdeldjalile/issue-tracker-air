@echo off
cd /d duplicate-detection
python -m venv dupenv
dupenv\Scripts\activate
pip install --upgrade pip
pip install -r requirements.txt
python dup.py
pause

