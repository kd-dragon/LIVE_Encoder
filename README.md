# LIVE_Encoder
Live Encoder SpringBoot Application


스프링부트 기반 라이브 인코더 모듈

FFMPEG Wrapper Library 를 사용하여 라이브 스트림을 HLS 파일로 인코딩 및 Write 합니다.

Input: RTMP/RTSP 스트림.

Output: HLS (M3U8, TS).

> WatchService 를 통해 생성/갱신되는 M3U8, TS파일을 바이너리 형태로 Redis 에 담아 스트리밍 서버로 전송


Live Record
> FFMPEG concat 명령어로 TS 파일을 MP4 로 인코딩 

> 부분(선택)녹화 -> 녹화활성화시 TS 파일 Redis List Push, 라이브 방송 이후 Redis List에 담긴 파일명만 txt파일 write, concat 처리