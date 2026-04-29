#!/usr/bin/env python3
"""
Startup script for Air Algérie AI Service
Runs both the main Flask app and Rasa actions server
"""
import os
import sys
import subprocess
import threading
import time
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def run_flask_app():
    """Run the main Flask application"""
    try:
        logger.info("Starting Flask AI service...")
        subprocess.run([
            sys.executable, "app.py"
        ], cwd=os.path.dirname(__file__))
    except Exception as e:
        logger.error(f"Flask app failed: {e}")

def run_actions_server():
    """Run the Rasa actions server"""
    try:
        logger.info("Starting Rasa actions server...")
        # Wait a moment for the main app to start
        time.sleep(2)
        subprocess.run([
            sys.executable, "-m", "rasa_sdk", "--actions", "actions_server"
        ], cwd=os.path.dirname(__file__))
    except Exception as e:
        logger.error(f"Rasa actions server failed: {e}")

if __name__ == "__main__":
    logger.info("Starting Air Algérie AI Service Suite")
    logger.info("=" * 50)

    # Start Flask app in background thread
    flask_thread = threading.Thread(target=run_flask_app, daemon=True)
    flask_thread.start()

    # Start actions server in main thread
    run_actions_server()

    logger.info("AI Service suite shutdown")