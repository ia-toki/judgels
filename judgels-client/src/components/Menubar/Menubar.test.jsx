import { act, render, screen } from '@testing-library/react';

import { TestRouter } from '../../test/RouterWrapper';
import Menubar from './Menubar';

describe('Menubar', () => {
  const items = [
    { id: 'first', title: 'First', route: { path: '/first' } },
    { id: 'second', title: 'Second', route: { path: '/second' } },
    { id: 'third', title: 'Third', route: { path: '/third' } },
  ];

  const homeRoute = { id: 'home', title: 'Home', route: { path: '/' } };

  const renderMenubar = async path => {
    await act(async () =>
      render(
        <TestRouter initialEntries={[path]}>
          <Menubar items={items} homeRoute={homeRoute} />
        </TestRouter>
      )
    );
  };

  it('renders all menu items', async () => {
    await renderMenubar('/');

    expect(screen.getByText('Home')).toBeInTheDocument();
    expect(screen.getByText('First')).toBeInTheDocument();
    expect(screen.getByText('Second')).toBeInTheDocument();
    expect(screen.getByText('Third')).toBeInTheDocument();
  });

  it('highlights the active route', async () => {
    await renderMenubar('/second');

    const tabs = screen.getAllByRole('tab');
    expect(tabs.find(t => t.textContent === 'Home')).toHaveAttribute('aria-selected', 'false');
    expect(tabs.find(t => t.textContent === 'Second')).toHaveAttribute('aria-selected', 'true');
  });

  it('highlights active route for nested paths', async () => {
    await renderMenubar('/second/child/path');

    const tabs = screen.getAllByRole('tab');
    expect(tabs.find(t => t.textContent === 'Second')).toHaveAttribute('aria-selected', 'true');
  });

  it('falls back to home when no route matches', async () => {
    await renderMenubar('/unknown');

    const tabs = screen.getAllByRole('tab');
    expect(tabs.find(t => t.textContent === 'Home')).toHaveAttribute('aria-selected', 'true');
  });

  it('generates correct links', async () => {
    await renderMenubar('/');

    expect(screen.getByRole('link', { name: 'Third' })).toHaveAttribute('href', '/third');
  });
});
