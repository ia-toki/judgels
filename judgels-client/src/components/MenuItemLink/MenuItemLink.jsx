import { MenuItem } from '@blueprintjs/core';
import { useNavigate } from '@tanstack/react-router';

export default function MenuItemLink({ text, to }) {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate({ to });
  };

  return <MenuItem text={text} onClick={handleClick} />;
}
