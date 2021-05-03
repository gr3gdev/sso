rm -f build/ssokeystore.jks build/public.txt
keytool -genkey -alias mysso \
 -keystore build/ssokeystore.jks \
 -storetype PKCS12 \
 -keyalg RSA \
 -validity 365 \
 -keysize 2048 \
 -dname "CN=Gregory Tardivel, OU=gr3gdev, S=France, C=FR" \
 -keypass myssoP@ssw0rd \
 -storepass myssoP@ssw0rd
keytool -list -rfc --keystore build/ssokeystore.jks \
 -storepass myssoP@ssw0rd | openssl x509 -inform pem -pubkey -noout > build/public.txt