import { Icon, Navbar } from '@blueprintjs/core';
import { Link } from '@tanstack/react-router';
import { useCallback, useEffect, useRef, useState } from 'react';

import logo from '../../assets/images/logo-header.png';
import { APP_CONFIG } from '../../conf';
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
      <div className="header__wrapper">
        <div className="header__left">
          <Link to="/">
            <img src={logo} alt="header" className="header__logo" />
          </Link>
          <div className="header__text">
            <div className="header__title">{APP_CONFIG.name}</div>
            <div className="header__subtitle">{APP_CONFIG.slogan}</div>
          </div>
        </div>

        <div className="header__center-wrapper">
          <div className="header__center" ref={centerRef} onScroll={updateScrollState}>
            <Menubar items={items} homeRoute={homeRoute} />
          </div>
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
        </div>

        <div className="header__right">
          <DarkModeWidget />
          <UserWidget items={items} homeRoute={homeRoute} />
        </div>
      </div>
    </Navbar>
  );
}
