kind: Deployment
apiVersion: apps/v1
metadata:
annotations:
build-commit: @git.commit.id.abbrev@
build-date: "@git.build.time@"
name: spring-boot-admin
labels:
app: spring-boot-admin
spec:
replicas: 2
selector:
matchLabels:
app: spring-boot-admin
template:
metadata:
name: podname
labels:
app: spring-boot-admin
annotations:
build-commit: @git.commit.id.abbrev@
build-date: "@git.build.time@"
spec:
volumes:
- name: logs
emptyDir: {}
containers:
- resources:
requests:
memory: {{project.memory}}
cpu : {{project.cpu}}
limits:
memory: {{project.memory}}
cpu : {{project.cpu}}
readinessProbe:
httpGet:
path: actuator/health
port: 8080
scheme: HTTP
name: microservice-pod
env:
- name: bil.isp-admin.log.path
valueFrom:
configMapKeyRef:
name: spring-boot-admin-config
key: bil.isp-admin.log.path
- name: spring.cloud.kubernetes.client.namespace
valueFrom:
configMapKeyRef:
name: spring-boot-admin-config
key: spring.cloud.kubernetes.client.namespace
- name: bil.zone
valueFrom:
configMapKeyRef:
name: spring-boot-admin-config
key: bil.zone
- name: bil.isp.sso.url
valueFrom:
configMapKeyRef:
name: spring-boot-admin-config
key: bil.isp.sso.url
- name: server.port
valueFrom:
configMapKeyRef:
name: spring-boot-admin-config
key: server.port
- name: bil.isp.sso.client_id
valueFrom:
secretKeyRef:
name: spring-boot-admin-secret
key: bil.isp.sso.client_id
- name: service-proxy-admin.log.path
valueFrom:
configMapKeyRef:
name: spring-boot-admin-config
key: service-proxy-admin.log.path
- name: spring.cloud.kubernetes.discovery.enabled
valueFrom:
configMapKeyRef:
name: spring-boot-admin-config
key: spring.cloud.kubernetes.discovery.enabled
- name: spring.cloud.kubernetes.discovery.catalogServicesWatchDelay
valueFrom:
configMapKeyRef:
name: spring-boot-admin-config
key: spring.cloud.kubernetes.discovery.catalogServicesWatchDelay
- name: bil.environment
valueFrom:
configMapKeyRef:
name: spring-boot-admin-config
key: bil.environment
- name: server.ssl.enabled
valueFrom:
configMapKeyRef:
name: spring-boot-admin-config
key: server.ssl.enabled
- name: bil.node-number
valueFrom:
configMapKeyRef:
name: spring-boot-admin-config
key: bil.node-number
- name: spring.codec.max-in-memory-size
valueFrom:
configMapKeyRef:
name: spring-boot-admin-config
key: spring.codec.max-in-memory-size
- name: bil.isp.registry.admin-role
valueFrom:
configMapKeyRef:
name: spring-boot-admin-config
key: bil.isp.registry.admin-role
- name: bil.isp.sso.client_secret
valueFrom:
secretKeyRef:
name: spring-boot-admin-secret
key: bil.isp.sso.client_secret
ports:
- containerPort: 8080
protocol: TCP
name: http
imagePullPolicy: Always
volumeMounts:
- name: logs
mountPath: /Logs
image: >-
{{ocp-cluster.registry}}/spring-boot-admin-ocp-dev/spring-boot-admin:@project.version@
restartPolicy: Always
---
kind: Service
apiVersion: v1
metadata:
name: spring-boot-admin-service
labels:
spring-boot: 'true'
spring-boot-env: dev
spec:
ports:
- name: http
protocol: TCP
port: 8080
targetPort: 8080
selector:
app: spring-boot-admin
---
kind: Route
apiVersion: route.openshift.io/v1
metadata:
name: spring-boot-admin
spec:
to:
kind: Service
name: spring-boot-admin-service
weight: 256
port:
targetPort: '8080'
tls:
termination: edge
wildcardPolicy: None
---
kind: ConfigMap
apiVersion: v1
metadata:
name: spring-boot-admin-config
data:
service-proxy-admin.log.path: "/Logs"
spring.cloud.kubernetes.client.namespace: ""
spring.codec.max-in-memory-size: "{{spring.codec.max-in-memory-size}}"
bil.environment: "{{bil.environment}}"
bil.node-number: "{{bil.node-number}}"
server.port: "{{server.port}}"
server.ssl.enabled: "false"
bil.isp.registry.admin-role: "{{bil.isp.registry.admin-role}}"
spring.cloud.kubernetes.discovery.enabled: "true"
bil.zone: "lan"
bil.isp-admin.log.path: "/Logs"
bil.isp.sso.url: "{{bil.isp.sso.url}}"
spring.cloud.kubernetes.discovery.catalogServicesWatchDelay: "1000"
---
kind: Secret
apiVersion: v1
metadata:
name: spring-boot-admin-secret
data:
bil.isp.sso.client_id: {{bil.isp.sso.client_id.b64}}
bil.isp.sso.client_secret: {{bil.isp.sso.client_secret.b64}}
type: generic
