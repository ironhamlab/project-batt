import Box from '@mui/joy/Box';
import { keyframes } from '@emotion/react';

// keyframes 애니메이션 정의
const fillUp = keyframes`
  0% {
    clip-path: polygon(0 100%, 100% 100%, 100% 100%, 0 100%);
  }
  100% {
    clip-path: polygon(0 0, 100% 0, 100% 100%, 0 100%);
  }
`;

const TicketLoader = () => {
  return (
    <Box
      sx={{
        width: 200,
        height: 100,
        backgroundColor: '#eee', // 티켓의 기본 색상
        position: 'relative',
        clipPath: 'polygon(0% 0%, 100% 0%, 100% 80%, 90% 100%, 10% 100%, 0% 80%)', // 티켓 모양
        '&::after': {
          content: '""',
          position: 'absolute',
          top: 0,
          left: 0,
          width: '100%',
          height: '100%',
          backgroundColor: '#3498db', // 채워질 색상
          animation: `${fillUp} 2s ease-in-out infinite`,
        },
      }}
    />
  );
};

export default TicketLoader;