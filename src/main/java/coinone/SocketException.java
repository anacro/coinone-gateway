package coinone;

import java.io.IOException;

@SuppressWarnings("serial")
public class SocketException extends RuntimeException {

	SocketError _socketErrorCode;

	public SocketException(SocketError errorCode) {	
		_socketErrorCode = errorCode;
	}
	
	public SocketException(SocketError errorCode, String message) {
		super(message);
		_socketErrorCode = errorCode;
	}	
	
	public SocketException(SocketError errorCode, Throwable t) {
		super(t);
		_socketErrorCode = errorCode;
	}
	
	public SocketError getSocketError() {
		return _socketErrorCode;
	}
	
	@Override
	public String getMessage() {
		return _socketErrorCode.toString() + ", " + super.getMessage();
	}
	
	public static final SocketException parse(Throwable ex) {		
		String msg = ex.getMessage().toLowerCase();
		if(msg.contains("connection timed out"))
			return new SocketException(SocketError.TimedOut);
		else if(msg.contains("connection refused"))
			return new SocketException(SocketError.ConnectionRefused);
		else if(msg.contains("connection reset") || msg.contains("closed by the remote host") || msg.contains("원격 호스트에 의해 강제로 끊겼습니다"))
			return new SocketException(SocketError.ConnectionReset);  
		else
			return new SocketException(SocketError.SocketError, ex);
	}
	
	public static boolean isIgnorableSocketError(Throwable t) {
		if(t instanceof IOException) {
			IOException ioe = (IOException) t;
			SocketException ex = parse(ioe);
			SocketError error = ex.getSocketError();
			if(error == SocketError.ConnectionReset 
					|| error == SocketError.ConnectionAborted
					|| error == SocketError.Shutdown
					|| error == SocketError.OperationAborted)
				return true;
			else
				return false;
		}
		else
			return false;
	}		
	
    public enum SocketError
    {    	
        // 요약:
        //     지정되지 않은 System.Net.Sockets.Socket 오류가 발생했습니다.
        SocketError(-1),
        //
        // 요약:
        //     System.Net.Sockets.Socket 작업을 성공적으로 완료했습니다.
        Success(0),
        //
        // 요약:
        //     System.Net.Sockets.Socket을 닫아서 겹쳐진 작업이 중단되었습니다.
        OperationAborted(995),
        //
        // 요약:
        //     응용 프로그램에서 즉시 완료할 수 없는 겹쳐진 작업을 시작했습니다.
        IOPending(997),
        //
        // 요약:
        //     블로킹 System.Net.Sockets.Socket 호출이 취소되었습니다.
        Interrupted(10004),
        //
        // 요약:
        //     지정된 액세스 권한에서 허용하지 않는 방식으로 System.Net.Sockets.Socket에 액세스하려고 시도했습니다.
        AccessDenied(10013),
        //
        // 요약:
        //     내부 소켓 공급자에서 잘못된 포인터 주소를 발견했습니다.
        Fault(10014),
        //
        // 요약:
        //     System.Net.Sockets.Socket 멤버에 잘못된 인수를 지정했습니다.
        InvalidArgument(10022),
        //
        // 요약:
        //     내부 소켓 공급자에 열려 있는 소켓이 너무 많습니다.
        TooManyOpenSockets(10024),
        //
        // 요약:
        //     비블로킹 소켓에 대한 작업을 즉시 완료할 수 없습니다.
        WouldBlock(10035),
        //
        // 요약:
        //     블로킹 작업이 진행 중입니다.
        InProgress(10036),
        //
        // 요약:
        //     비블로킹 System.Net.Sockets.Socket 작업이 이미 진행 중입니다.
        AlreadyInProgress(10037),
        //
        // 요약:
        //     소켓이 아닌 위치에서 System.Net.Sockets.Socket 작업을 시도했습니다.
        NotSocket(10038),
        //
        // 요약:
        //     System.Net.Sockets.Socket 작업에 필수 주소가 누락되었습니다.
        DestinationAddressRequired(10039),
        //
        // 요약:
        //     데이터그램이 너무 깁니다.
        MessageSize(10040),
        //
        // 요약:
        //     이 System.Net.Sockets.Socket의 프로토콜 형식이 잘못되었습니다.
        ProtocolType(10041),
        //
        // 요약:
        //     알 수 없거나), 잘못되거나), 지원되지 않는 옵션 또는 수준을 System.Net.Sockets.Socket에 사용했습니다.
        ProtocolOption(10042),
        //
        // 요약:
        //     프로토콜이 구현되지 않거나 구성되지 않았습니다.
        ProtocolNotSupported(10043),
        //
        // 요약:
        //     이 주소 패밀리에서는 지정된 소켓 형식이 지원되지 않습니다.
        SocketNotSupported(10044),
        //
        // 요약:
        //     주소 패밀리가 프로토콜 패밀리에서 지원되지 않습니다.
        OperationNotSupported(10045),
        //
        // 요약:
        //     프로토콜 패밀리가 구현되지 않거나 구성되지 않았습니다.
        ProtocolFamilyNotSupported(10046),
        //
        // 요약:
        //     지정된 주소 패밀리가 지원되지 않습니다. IPv6 주소 패밀리가 지정되었는데 IPv6 스택이 로컬 컴퓨터에 설치되어 있지 않은 경우
        //     이 오류가 반환됩니다. 또한 IPv4 주소 패밀리가 지정되었는데 IPv4 스택이 로컬 컴퓨터에 설치되어 있지 않은 경우에도 이 오류가
        //     반환됩니다.
        AddressFamilyNotSupported(10047),
        //
        // 요약:
        //     일반적으로 같은 주소는 한 번만 사용할 수 있습니다.
        AddressAlreadyInUse(10048),
        //
        // 요약:
        //     선택한 IP 주소가 이 컨텍스트에서 유효하지 않습니다.
        AddressNotAvailable(10049),
        //
        // 요약:
        //     네트워크를 사용할 수 없습니다.
        NetworkDown(10050),
        //
        // 요약:
        //     원격 호스트의 경로가 존재하지 않습니다.
        NetworkUnreachable(10051),
        //
        // 요약:
        //     응용 프로그램에서 시간이 초과된 연결에 System.Net.Sockets.SocketOptionName.KeepAlive를 설정하려고
        //     했습니다.
        NetworkReset(10052),
        //
        // 요약:
        //     .NET Framework 또는 내부 소켓 공급자에 의해 연결이 끊어졌습니다.
        ConnectionAborted(10053),

        /**
         * 원격 피어가 연결을 다시 설정했습니다<br/>
         * 연결 된 상태에서 상대방으로 부터 RST 패킷 받음, 비정상 종료(프로그램이 강제 종료되는 경우 끊겼음을 알려주기 위해 OS가 보내줌)
         */
        ConnectionReset(10054),
        
        //
        // 요약:
        //     System.Net.Sockets.Socket 작업에 사용할 수 있는 여유 버퍼 공간이 없습니다.
        NoBufferSpaceAvailable(10055),
        //
        // 요약:
        //     System.Net.Sockets.Socket이 이미 연결되어 있습니다.
        IsConnected(10056),
        //
        // 요약:
        //     System.Net.Sockets.Socket이 연결되지 않은 상태로 응용 프로그램에서 데이터를 보내고 받으려고 했습니다.
        NotConnected(10057),
        //
        // 요약:
        //     System.Net.Sockets.Socket이 이미 닫혔기 때문에 데이터를 보내거나 받기 위한 요청이 거부되었습니다.
        Shutdown(10058),

        /**
         * 연결 시도 제한 시간이 초과되었거나 연결된 호스트에서 응답하지 않습니다<br/>
         * 연결 시도 했는데 타임아웃, 존재하지 않는 IP
         */
        TimedOut(10060),
        
        /**
         * 원격 호스트가 연결을 거부했습니다. <br/>
         * The port is not open on the destination machine(연결 시도 했는데 상대방으로 부터 RST 패킷이 옴)  <br/>
         * The port is open on the destination machine, but its backlog of pending connections is full.  <br/>
         * A firewall between the client and server is blocking access (also check local firewalls).  <br/>
         */
        ConnectionRefused(10061),
        
        //
        // 요약:
        //     원격 호스트가 다운되어 작업이 실패했습니다.
        HostDown(10064),
        //
        // 요약:
        //     지정된 호스트에 대한 네트워크 경로가 존재하지 않습니다.
        HostUnreachable(10065),
        //
        // 요약:
        //     내부 소켓 공급자를 사용하는 프로세스가 너무 많습니다.
        ProcessLimit(10067),
        //
        // 요약:
        //     네트워크 하위 시스템을 사용할 수 없습니다.
        SystemNotReady(10091),
        //
        // 요약:
        //     내부 소켓 공급자의 버전이 범위를 벗어났습니다.
        VersionNotSupported(10092),
        //
        // 요약:
        //     내부 소켓 공급자가 초기화되지 않았습니다.
        NotInitialized(10093),
        //
        // 요약:
        //     정상적으로 종료하는 중입니다.
        Disconnecting(10101),
        //
        // 요약:
        //     지정된 클래스를 찾을 수 없습니다.
        TypeNotFound(10109),
        //
        // 요약:
        //     호스트를 확인할 수 없습니다. 이름이 공식 호스트 이름 또는 별칭이 아닙니다.
        HostNotFound(11001),
        //
        // 요약:
        //     호스트 이름을 확인할 수 없습니다. 나중에 다시 시도하십시오.
        TryAgain(11002),
        //
        // 요약:
        //     오류를 복구할 수 없거나 요청된 데이터베이스를 찾을 수 없습니다.
        NoRecovery(11003),
        //
        // 요약:
        //     요청된 이름 또는 IP 주소를 이름 서버에서 찾을 수 없습니다.
        NoData(11004);
        
		int _errorCode;
		SocketError(int errorCode) {
			_errorCode = errorCode;		
		}
		
		public int getCode() {
			return _errorCode;
		}
		
    }	

}
