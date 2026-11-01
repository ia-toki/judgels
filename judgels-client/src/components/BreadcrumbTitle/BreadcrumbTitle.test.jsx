import { Home } from '@blueprintjs/icons';
import { render, screen } from '@testing-library/react';

import { TestRouter } from '../../test/RouterWrapper';
import { BreadcrumbTitle } from './BreadcrumbTitle';

describe('BreadcrumbTitle', () => {
  const renderComponent = async () => {
    render(
      <TestRouter>
        <h2>
          <BreadcrumbTitle
            items={[
              { icon: <Home title="My course" />, to: '/courses/my-course' },
              { text: 'Chapter', to: '/courses/my-course/chapters/A' },
              { text: 'Lesson', to: '/courses/my-course/chapters/A/lessons/B' },
            ]}
          />
        </h2>
      </TestRouter>
    );
    await screen.findByRole('link', { name: 'Lesson' });
  };

  it('links every item, with the last one as the current page', async () => {
    await renderComponent();

    expect(screen.getByRole('link', { name: 'My course' })).toHaveAttribute('href', '/courses/my-course');
    expect(screen.getByRole('link', { name: 'Chapter' })).toHaveAttribute('href', '/courses/my-course/chapters/A');
    expect(screen.getByRole('link', { name: 'Chapter' })).not.toHaveClass('bp6-breadcrumb-current');
    expect(screen.getByRole('link', { name: 'Lesson' })).toHaveAttribute(
      'href',
      '/courses/my-course/chapters/A/lessons/B'
    );
    expect(screen.getByRole('link', { name: 'Lesson' })).toHaveClass('bp6-breadcrumb-current');
  });

  it('keeps the items as the name of the heading around it', async () => {
    await renderComponent();

    expect(screen.getByRole('heading', { name: /Chapter.*Lesson/ })).toBeInTheDocument();
  });
});
