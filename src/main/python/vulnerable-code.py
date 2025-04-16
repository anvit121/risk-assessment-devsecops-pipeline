import subprocess
import hashlib

# Insecure subprocess call
subprocess.call("rm -rf /", shell=True)

# Hardcoded credentials
username = "admin"
password = "admin123"

# Insecure hashing
hashlib.md5(password.encode()).hexdigest()
