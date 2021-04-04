import React from 'react';

import { useInterval } from 'ahooks';

export const Gif: React.FC<{ interval: number }> = ({ interval }) => {
  const [idx, setIdx] = React.useState(0);
  const [imgs] = React.useState([
    require('@/assets/g1.png'),
    require('@/assets/g2.png'),
    require('@/assets/g3.png'),
  ]);
  useInterval(() => {
    setIdx((idx + 1) % imgs.length);
  }, interval);

  return <img src={imgs[idx]}></img>;
};
