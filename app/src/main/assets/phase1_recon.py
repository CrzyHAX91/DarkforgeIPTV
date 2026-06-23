#!/usr/bin/env python3
# ==============================================================================
# DARKFORGE-X: RECONNAISSANCE MODE (PHASE 1)
# MODULE: Advanced Stealth Enumeration & Fingerprinting
# AUTHORIZATION: Authorized Penetration Testing Operations Only.
# ==============================================================================

import socket
import argparse
import time
import json
import logging
from concurrent.futures import ThreadPoolExecutor

logging.basicConfig(level=logging.INFO, format='[DARKFORGE] [+] %(message)s')

class ReconMatrix:
    def __init__(self, target, start_port, end_port, threads=200):
        self.target = target
        self.start_port = start_port
        self.end_port = end_port
        self.threads = threads
        self.results = {}
        logging.info(f"Target locked: {self.target} | Port Range: {self.start_port}-{self.end_port}")

    def capture_banner(self, sock):
        try:
            # Send anomalous payload to trigger verbose application layer responses
            payload = b"GET / HTTP/1.1\r\nHost: shadow.core\r\nUser-Agent: DarkForge-X/1.0\r\n\r\n"
            sock.send(payload)
            banner = sock.recv(2048).decode('utf-8', errors='ignore').strip()
            return banner.split('\n')[0] if banner else "ACK_NO_DATA"
        except socket.timeout:
            return "TIMEOUT"
        except Exception:
            return "ERR_STREAM_RESET"

    def scan_target_port(self, port):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(1.5)
        try:
            result = sock.connect_ex((self.target, port))
            if result == 0:
                banner = self.capture_banner(sock)
                self.results[port] = {"status": "OPEN", "banner": banner[:100]}
                logging.info(f"Port {port}/TCP -> OPEN | Signature: {self.results[port]['banner']}")
        except Exception as e:
            pass
        finally:
            sock.close()

    def execute_assault(self):
        logging.info("Initiating high-concurrency stealth syn-ack synthesis...")
        start_time = time.time()
        
        with ThreadPoolExecutor(max_workers=self.threads) as executor:
            executor.map(self.scan_target_port, range(self.start_port, self.end_port + 1))
            
        duration = time.time() - start_time
        logging.info("Scan trajectory complete.")
        logging.info(f"Chronology: {duration:.2f} seconds. Discovered {len(self.results)} open vectors.")
        
        return json.dumps(self.results, indent=4)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="DarkForge-X Reconnaissance Module")
    parser.add_argument("-t", "--target", type=str, required=True, help="Target IP or Hostname")
    parser.add_argument("-s", "--start", type=int, default=1, help="Start port")
    parser.add_argument("-e", "--end", type=int, default=1024, help="End port")
    parser.add_argument("-w", "--workers", type=int, default=250, help="Concurrency level")
    args = parser.parse_args()

    matrix = ReconMatrix(target=args.target, start_port=args.start, end_port=args.end, threads=args.workers)
    report = matrix.execute_assault()
    
    with open(f"recon_{args.target}.json", "w") as f:
         f.write(report)
         logging.info(f"Data exfiltrated to recon_{args.target}.json")
