network domain 설명  
Api : HttpMethod, body, Url   
Url : Protocol, Host, Port, Uri  
uri : uri. pattern  

Server : 현재 가동 중인 물리적 Server  
Service : 제공중인 Service (ex : user, part Service)  
Pod : Service를 실행중인 Container (ex : Docker Container)

```mermaid
classDiagram
    class Api {
        - method: HttpMethod
        - body: String
        - url: Url
    }
    class Url {
        - protocol: Protocol
        - host: Host
        - port: Prot
        - uri: Uri
    }
    class Uri {
        - uri: String
    }
    
    class Server {
        - name: String
        - host: String
        - protocol: String
        - count: int
    }
    
    note for Server "
        현재 가동 중인 물리적 Server
    "
    
    class Service {
        - matchingUris: List<Uri>
        - runningPods: List
        - subPod: List
        + matches(Uri requestUri): boolean
    }
    
    note for Service "
        제공중인 Service 
        (ex : user, part Service)
    "

    class Pod {
        - name: String
        - Server: server
        - Prot: prot
        - boolean: isRunning
    }
    
    note for Pod "
        Service를 실행중인 Container 
        (ex : Docker Container)
    "

    Server --> Service : 제공중인 Service들
    Service --> Pod : ScaleOut된 Pods
    
    Api -- Url
    Url -- Uri

```