import { useState } from 'react';
import { FontList } from './components/FontList';
import { FontDetail } from './components/FontDetail';

export function FontListPage() {
  const [font, setFont] = useState({ fontFaceId: -1 });
  return (
    <div>
      {font.fontFaceId < 0 ? (
        <FontList onSelect={(id) => setFont({ fontFaceId: id })} />
      ) : (
        <FontDetail
          onBack={() => setFont({ fontFaceId: -1 })}
          fontFaceId={font.fontFaceId}
        />
      )}
    </div>
  );
}
