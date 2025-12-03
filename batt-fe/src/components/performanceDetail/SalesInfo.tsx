import { useEffect, useRef } from "react";
import styles from "../../styles/SalesInfo.module.css";

declare global {
  interface Window {
    naver: any;
  }
}

// 멀티캠퍼스 역삼 근처 좌표
const NAVER_MAP_CENTER = { lat: 37.5013, lng: 127.0369 };

const SalesInfo = () => {
  const mapContainerRef = useRef<HTMLDivElement | null>(null);
  const clientId = import.meta.env.VITE_NAVER_MAP_CLIENT_ID;

  useEffect(() => {
    // 컨테이너가 없으면 초기화 중단
    if (!mapContainerRef.current) return;

    // 지도 초기화 함수
    const initializeMap = () => {
      if (!window.naver || !window.naver.maps) return;

      const center = new window.naver.maps.LatLng(
        NAVER_MAP_CENTER.lat,
        NAVER_MAP_CENTER.lng,
      );

      const map = new window.naver.maps.Map(mapContainerRef.current, {
        center,
        zoom: 16,
      });

      new window.naver.maps.Marker({
        position: center,
        map,
      });
    };

    // 이미 네이버 맵 스크립트가 로드되어 있다면 바로 초기화
    if (window.naver && window.naver.maps) {
      initializeMap();
      return;
    }

    // 환경변수에 키가 없으면 중단 (콘솔 확인용)
    if (!clientId) {
      console.error("VITE_NAVER_MAP_CLIENT_ID 가 설정되지 않았습니다.");
      return;
    }

    // 동일 스크립트가 중복 로드되지 않도록 기존 것을 먼저 확인
    const EXISTING_ID = "naver-maps-script";
    const existing = document.getElementById(
      EXISTING_ID,
    ) as HTMLScriptElement | null;

    if (existing) {
      // 이미 추가됐으면 onload만 연결 후 종료
      if (window.naver && window.naver.maps) {
        initializeMap();
      } else {
        existing.addEventListener("load", initializeMap, { once: true });
      }
      return;
    }

    // 공식 문서 기준: oapi.map.naver.com 사용, ncpKeyId 파라미터 사용
    const script = document.createElement("script");
    script.id = EXISTING_ID;
    script.src = `https://oapi.map.naver.com/openapi/v3/maps.js?ncpKeyId=${clientId}`;
    script.async = true;
    script.defer = true;
    script.addEventListener("load", initializeMap, { once: true });
    script.addEventListener("error", () => {
      console.error("네이버 지도 스크립트 로드 실패");
    });
    document.head.appendChild(script);

    // 언마운트 시 리스너 제거 (스크립트는 유지)
    return () => {
      script.removeEventListener("load", initializeMap);
    };
  }, [clientId]);

  return (
    <div className={styles.container}>
      <div className={styles.section}>
        <h2 className={styles.sectionTitle}>공연장 정보</h2>
        <div className={styles.infoBlock}>
          <p>
            <strong>공연장 이름:</strong> 멀티 아트센터 역삼
          </p>
          <div
            ref={mapContainerRef}
            style={{ width: "100%", height: "360px", borderRadius: "8px" }}
          />
        </div>
      </div>
    </div>
  );
};

export default SalesInfo;
