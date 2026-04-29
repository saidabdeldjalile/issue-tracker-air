#!/usr/bin/env python3
"""
Rasa Training Script for Air Algérie Chatbot
"""
import os
import sys
import logging
from pathlib import Path

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def train_rasa_model():
    """Train the Rasa model using the configuration and training data"""

    try:
        from rasa import train
        from rasa.core.utils import configure_file_importer

        # Set the working directory to the ai-service folder
        os.chdir(Path(__file__).parent)

        logger.info("Starting Rasa model training...")

        # Train the model
        train.train(
            domain="domain.yml",
            config="config.yml",
            training_files=["data"],
            output="models",
            force_training=True,
            fixed_model_name="air_algerie_chatbot"
        )

        logger.info("Rasa model training completed successfully!")

    except ImportError as e:
        logger.error(f"Rasa not installed: {e}")
        logger.info("Please install Rasa: pip install rasa")
        sys.exit(1)
    except Exception as e:
        logger.error(f"Training failed: {e}")
        sys.exit(1)

def train_actions():
    """Train the custom actions if needed"""
    logger.info("Training custom actions...")
    # Custom actions don't need separate training, they're code-based
    logger.info("Custom actions ready!")

if __name__ == "__main__":
    logger.info("Air Algérie Chatbot Training Script")
    logger.info("=" * 40)

    train_rasa_model()
    train_actions()

    logger.info("Training completed! Model saved in models/ directory")