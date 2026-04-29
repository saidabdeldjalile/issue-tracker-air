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

if __name__ == "__main__":
    logger.info("Starting Air Algérie AI Service (Qwen 2.5)")
    logger.info("=" * 50)
    
    # Run the main Flask app
    try:
        subprocess.run([
            sys.executable, "app.py"
        ], cwd=os.path.dirname(__file__))
    except KeyboardInterrupt:
        logger.info("Shutting down...")
    except Exception as e:
        logger.error(f"Failed to start AI service: {e}")