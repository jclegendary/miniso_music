import bg from '@/assets/bg.jpg';
import '@/global.less';
import React from 'react';
import btn from '@/assets/play.png';
import { useBoolean, useMount } from 'ahooks';
import { Gif } from './components';
import m3 from '@/assets/music/m3.mp3';
import m2 from '@/assets/music/m2.mp3';
import m1 from '@/assets/music/m1.mp3';

console.log(123);
// 组件库
export const useWinSize = () => {
  const [size, setSize] = React.useState({
    width: document.documentElement.clientWidth,
    height: document.documentElement.clientHeight,
  });
  const onResize = React.useCallback(() => {
    setSize({
      width: document.documentElement.clientWidth,
      height: document.documentElement.clientHeight,
    });
  }, []);
  React.useEffect(() => {
    window.addEventListener('resize', onResize);
    return () => {
      window.removeEventListener('resize', onResize);
    };
  }, []);
  return size;
};

const Com: React.FC = () => {
  const win = useWinSize();
  let scale = 1;

  const imgRef = React.useRef<HTMLImageElement>(null);
  const [img, setImgSize] = React.useState({
    width: 1,
    height: 1,
  });
  const winPre = win.width / win.height;
  const imgPre = img.width / img.height;
  if (winPre >= imgPre) {
    scale = win.width / img.width;
  } else {
    scale = win.height / img.height;
  }

  const [isPlay, { setTrue, setFalse }] = useBoolean(false);

  const audioRef = React.useRef<HTMLAudioElement>(null);

  const audios: string[] = [m1, m2, m3];
  const [mIdx, setMIdx] = React.useState<number>(0);

  const play = () => {
    setTrue();
    audioRef.current?.play();
  };
  return (
    <div
      style={{
        width: '100vw',
        height: '100vh',
        position: 'relative',
        overflow: 'hidden',
      }}
    >
      <div
        style={{
          width: `${img.width}px`,
          height: `${img.height}px`,
          position: 'absolute',
          top: '50%',
          left: '50%',
          transform: `translate(-50%,-50%) scale(${scale})`,
        }}
      >
        <img
          ref={imgRef}
          onLoad={(e) => {
            if (imgRef.current) {
              setImgSize({
                width: imgRef.current.naturalWidth,
                height: imgRef.current.naturalHeight,
              });
            }
          }}
          src={bg}
        ></img>
        <div
          style={{
            position: 'absolute',
            left: '1232px',
            top: '455px',
            width: '400px',
          }}
        >
          {isPlay ? (
            <Gif interval={300}> </Gif>
          ) : (
            <img src={btn} onClick={play}></img>
          )}
        </div>
        <div>
          <audio
            style={{ display: 'none' }}
            onEnded={() => {
              setFalse();
              setMIdx(Math.floor(Math.random() * audios.length));
            }}
            ref={audioRef}
            src={audios[mIdx]}
          ></audio>
        </div>
      </div>
    </div>
  );
};

export default Com;
