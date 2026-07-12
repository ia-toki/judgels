import { render } from '@testing-library/react';

import { HtmlText } from './HtmlText';

describe('HtmlText', () => {
  const profilesMap = {
    userJid123: { username: 'user1' },
    userJid456: { username: 'user2' },
  };

  const renderComponent = children => render(<HtmlText profilesMap={profilesMap}>{children}</HtmlText>);

  test('renders user refs', () => {
    const { container } = renderComponent('<p>Hello [user:user1] and [user:user2], I mean [user:user1]!</p>');

    const links = container.querySelectorAll('a');
    expect([...links].map(link => link.textContent)).toEqual(['user1', 'user2', 'user1']);
    expect([...links].map(link => link.getAttribute('href'))).toEqual([
      '/profiles/user1',
      '/profiles/user2',
      '/profiles/user1',
    ]);
    expect(container.textContent).not.toContain('[user:');
  });
});
