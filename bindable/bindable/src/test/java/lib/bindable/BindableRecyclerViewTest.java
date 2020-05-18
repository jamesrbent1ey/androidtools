package lib.bindable;

import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BindableRecyclerViewTest
{
    @Mock
    MockScopeContext contextMock;
    @Mock
    AttributeSet     attributeSetMock;
    @Mock
    View             viewMock;
    @Mock
    KeyEvent         keyEventMock;

    @Before
    public void setup()
    {
        contextMock = mock(MockScopeContext.class);
        attributeSetMock = mock(AttributeSet.class);
        when(attributeSetMock.getAttributeValue(anyString(),anyString())).thenReturn("test");
        when(attributeSetMock.getAttributeCount()).thenReturn(1);
        when(attributeSetMock.getAttributeName(anyInt())).thenReturn("test");

        when(contextMock.getModel(anyString())).thenReturn("test");

        viewMock = mock(View.class);
        keyEventMock = mock(KeyEvent.class);
    }

    @Test
    public void getExportableAttributes()
    {
        BindableRecyclerView underTest = new BindableRecyclerView(contextMock, attributeSetMock);
        AttributeSet map = underTest.getExportableAttributes();
        assertNotNull(map);
        assertTrue(map.getAttributeCount() == 1);
    }

    @Test
    public void getScope()
    {
        BindableRecyclerView underTest = new BindableRecyclerView(contextMock, attributeSetMock);
        assertEquals(contextMock,underTest.getScope());
    }
}