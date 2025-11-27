import pika
import json
import time
import sys
import os
from datetime import datetime

if not os.path.exists("config.json"):
    print("Config file not found.")
    sys.exit(1)

with open("config.json", 'r') as f:
    config = json.load(f)

RABBIT_HOST = config.get("rabbit_host", "localhost")
QUEUE_NAME = config.get("queue_name", "device_measurements_queue")
DEVICE_ID = config.get("device_id")
INTERVAL_SECONDS = config.get("interval_seconds", 10)

def send_measurement(channel):
    timestamp = datetime.now().strftime("%Y-%m-%dT%H:%M:%S")

    measurement = {
        "timestamp": timestamp,
        "deviceId": DEVICE_ID,
        "measurementValue": round(1 + 3 * time.time() % 1, 2)
    }

    message = json.dumps(measurement)
    channel.basic_publish(exchange="", routing_key=QUEUE_NAME, body=message.encode())

    print(f"[SENT] {message}")

def main():
    print("Connecting to RabbitMQ...")
    
    connection = pika.BlockingConnection(pika.ConnectionParameters(host=RABBIT_HOST))
    channel = connection.channel()
    channel.queue_declare(queue=QUEUE_NAME, durable=True)

    print(f"Simulator started.\nDevice ID: {DEVICE_ID}\nSending every {INTERVAL_SECONDS} seconds...\n")

    try:
        while True:
            send_measurement(channel)
            time.sleep(INTERVAL_SECONDS)
    except KeyboardInterrupt:
        print("\nSimulator stopped.")
    finally:
        connection.close()

if __name__ == "__main__":
    main()