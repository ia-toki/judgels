import { MenuItem } from '@blueprintjs/core';
import { useNavigate } from 'react-router-dom';

export default function MenuItemLink({ text, to }) {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(to);
  };

  return <MenuItem text={text} onClick={handleClick} />;
}
