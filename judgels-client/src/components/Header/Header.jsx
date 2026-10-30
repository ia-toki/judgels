import { Icon, Navbar } from '@blueprintjs/core';
import { Box, Flex } from '@blueprintjs/labs';
import { Link } from '@tanstack/react-router';
import { useCallback, useEffect, useRef, useState } from 'react';

import logo from '../../assets/images/logo-header.png';
import { getAppName, getAppSlogan } from '../../modules/webConfig';
import DarkModeWidget from '../DarkModeWidget/DarkModeWidget';
import Menubar from '../Menubar/Menubar';
import UserWidget from '../UserWidget/UserWidget';

import './Header.scss';

export default function Header({ items, homeRoute }) {
  const [scrolledLeft, setScrolledLeft] = useState(false);
  const [scrolledRight, setScrolledRight] = useState(false);
  const centerRef = useRef(null);

  const updateScrollState = useCallback(() => {
    const el = centerRef.current;
    if (!el) return;
    setScrolledLeft(el.scrollLeft > 0);
    setScrolledRight(el.scrollLeft + el.clientWidth < el.scrollWidth - 1);
  }, []);

  useEffect(() => {
    const el = centerRef.current;
    if (!el) {
      return;
    }
    updateScrollState();
    const observer = new ResizeObserver(updateScrollState);
    observer.observe(el);
    return () => observer.disconnect();
  }, [updateScrollState]);

  return (
    <Navbar className="header">
      <Flex>
        <Flex className="header__left" gap={4} alignItems="center">
          <Link to="/">
            <img src={logo} alt="header" className="header__logo" />
          </Link>
          <Flex className="header__name" flexDirection="column">
            <div className="header__title">{getAppName()}</div>
            <div className="header__subtitle">{getAppSlogan()}</div>
          </Flex>
        </Flex>

        <Box className="header__center-wrapper" flex={1}>
          <Flex className="header__center" overflowX="auto" ref={centerRef} onScroll={updateScrollState}>
            <Menubar items={items} homeRoute={homeRoute} />
          </Flex>
          {scrolledLeft && (
            <button
              className="header__chevron header__chevron--left"
              onClick={() => centerRef.current?.scrollBy({ left: -120, behavior: 'smooth' })}
            >
              <Icon icon="chevron-left" />
            </button>
          )}
          {scrolledRight && (
            <button
              className="header__chevron header__chevron--right"
              onClick={() => centerRef.current?.scrollBy({ left: 120, behavior: 'smooth' })}
            >
              <Icon icon="chevron-right" />
            </button>
          )}
        </Box>

        <Flex flexShrink={0} alignItems="center">
          <DarkModeWidget />
          <UserWidget items={items} homeRoute={homeRoute} />
        </Flex>
      </Flex>
    </Navbar>
  );
}
