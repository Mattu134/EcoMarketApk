package com.example.ecomarketapk.viewmodel

import android.content.Context
import com.example.ecomarketapk.model.Usuario
import com.example.ecomarketapk.repository.UserRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class AuthViewModelTest {

    @Mock
    private lateinit var mockUserRepository: UserRepository

    @Mock
    private lateinit var mockContext: Context

    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authViewModel = AuthViewModel(mockUserRepository)
    }

//Test Login
    @Test
    fun `login con credenciales correctas deberia retornar true, actualizar usuario y no ser invitado`() {
        val email = "test@example.com"
        val password = "password123"
        val usuario = Usuario("Test User", email, "dirección", "12345678-9", password, "cliente")

        whenever(mockUserRepository.login(any(), eq(email), eq(password))).thenReturn(usuario)

        val resultado = authViewModel.login(mockContext, email, password)

        assertTrue(resultado)
        assertEquals(usuario, authViewModel.usuarioActual.value)
        assertFalse("No debería marcarse como invitado después de un login normal", authViewModel.esInvitado.value)
        assertEquals("", authViewModel.mensaje.value)
        assertTrue("puedeComprar debería ser true para cliente logueado", authViewModel.puedeComprar())
    }

    @Test
    fun `login con email en blanco deberia retornar false y mostrar mensaje de error`() {
        val resultado = authViewModel.login(mockContext, "", "password123")

        assertFalse(resultado)
        assertEquals("Completa todos los campos", authViewModel.mensaje.value)
        assertNull(authViewModel.usuarioActual.value)
        assertFalse(authViewModel.esInvitado.value)
        assertFalse(authViewModel.puedeComprar())
    }

    @Test
    fun `login con password en blanco deberia retornar false y mostrar mensaje de error`() {
        val resultado = authViewModel.login(mockContext, "test@example.com", "")

        assertFalse(resultado)
        assertEquals("Completa todos los campos", authViewModel.mensaje.value)
        assertNull(authViewModel.usuarioActual.value)
        assertFalse(authViewModel.esInvitado.value)
        assertFalse(authViewModel.puedeComprar())
    }

    @Test
    fun `login con email invalido deberia retornar false y mostrar mensaje de error`() {
        val resultado = authViewModel.login(mockContext, "email-invalido", "password123")

        assertFalse(resultado)
        assertEquals("Email inválido", authViewModel.mensaje.value)
        assertNull(authViewModel.usuarioActual.value)
        assertFalse(authViewModel.esInvitado.value)
        assertFalse(authViewModel.puedeComprar())
    }

    @Test
    fun `login con credenciales incorrectas deberia retornar false y mostrar mensaje de error`() {
        whenever(mockUserRepository.login(any(), eq("test@example.com"), eq("wrongpassword"))).thenReturn(null)

        val resultado = authViewModel.login(mockContext, "test@example.com", "wrongpassword")

        assertFalse(resultado)
        assertEquals("Email o contraseña incorrectos", authViewModel.mensaje.value)
        assertNull(authViewModel.usuarioActual.value)
        assertFalse(authViewModel.esInvitado.value)
        assertFalse(authViewModel.puedeComprar())
    }

//Test Registro
    @Test
    fun `registro exitoso deberia retornar true, actualizar usuario y no ser invitado`() {
        // Se utiliza un RUT válido.
        val usuario = Usuario("Nuevo Usuario", "nuevo@example.com", "dir", "11222333-9", "pass", "cliente")
        whenever(mockUserRepository.registrarUsuario(any(), eq(usuario))).thenReturn(true)

        val resultado = authViewModel.registrar(
            mockContext,
            usuario.nombre,
            usuario.email,
            usuario.direccion,
            usuario.rut,
            usuario.password
        )

        assertTrue("El registro falló porque el RUT '${usuario.rut}' fue considerado inválido.", resultado)
        assertEquals("Usuario registrado correctamente", authViewModel.mensaje.value)
        assertEquals(usuario.email, authViewModel.usuarioActual.value?.email)
        assertFalse(authViewModel.esInvitado.value)
        assertTrue("puedeComprar debería ser true para cliente registrado", authViewModel.puedeComprar())
    }

    @Test
    fun `registro con campos en blanco deberia retornar false y mostrar mensaje de error`() {
        val resultado = authViewModel.registrar(
            mockContext,
            "",
            "nuevo@example.com",
            "dir",
            "11222333-9",
            "pass"
        )

        assertFalse(resultado)
        assertEquals("Completa todos los campos", authViewModel.mensaje.value)
        assertNull(authViewModel.usuarioActual.value)
        assertFalse(authViewModel.esInvitado.value)
        assertFalse(authViewModel.puedeComprar())
    }

    @Test
    fun `registro con email invalido deberia retornar false y mostrar mensaje de error`() {
        // Se utiliza un RUT válido para enfocar la prueba en el email inválido.
        val resultado = authViewModel.registrar(
            mockContext,
            "Nombre",
            "email-invalido",
            "dir",
            "11222333-9",
            "pass"
        )

        assertFalse(resultado)
        assertEquals("Email inválido", authViewModel.mensaje.value)
        assertNull(authViewModel.usuarioActual.value)
        assertFalse(authViewModel.esInvitado.value)
        assertFalse(authViewModel.puedeComprar())
    }

    @Test
    fun `registro con RUT invalido deberia retornar false y mostrar mensaje de error`() {
        val resultado = authViewModel.registrar(
            mockContext,
            "Nombre",
            "nuevo@example.com",
            "dir",
            "123",
            "pass"
        )

        assertFalse(resultado)
        assertEquals("RUT inválido", authViewModel.mensaje.value)
        assertNull(authViewModel.usuarioActual.value)
        assertFalse(authViewModel.esInvitado.value)
        assertFalse(authViewModel.puedeComprar())
    }

    @Test
    fun `registro con usuario ya existente deberia retornar false y mostrar mensaje de error`() {
        // Se utiliza Rut valido
        val usuarioExistente = Usuario(
            "Usuario Existente",
            "existente@example.com",
            "dir",
            "22333444-K",
            "pass",
            "cliente"
        )
        whenever(mockUserRepository.registrarUsuario(any(), eq(usuarioExistente))).thenReturn(false)

        val resultado = authViewModel.registrar(
            mockContext,
            usuarioExistente.nombre,
            usuarioExistente.email,
            usuarioExistente.direccion,
            usuarioExistente.rut,
            usuarioExistente.password
        )

        assertFalse(resultado)
        assertEquals(
            "Se esperaba 'El usuario ya existe' pero el mensaje fue '${authViewModel.mensaje.value}'",
            "El usuario ya existe",
            authViewModel.mensaje.value
        )
        assertNull(authViewModel.usuarioActual.value)
        assertFalse(authViewModel.esInvitado.value)
        assertFalse(authViewModel.puedeComprar())
    }
    //Test Invitado, Logout y puedeComprar
    @Test
    fun `loginInvitado deberia crear usuario invitado, marcar esInvitado y no permitir comprar`() {
        authViewModel.loginInvitado()

        val user = authViewModel.usuarioActual.value

        assertNotNull("usuarioActual no debería ser null tras loginInvitado", user)
        assertEquals("Invitado", user?.nombre)
        assertEquals("invitado", user?.rol)
        assertTrue("esInvitado debería ser true luego de loginInvitado", authViewModel.esInvitado.value)
        assertEquals("", authViewModel.mensaje.value)
        assertFalse("puedeComprar debe ser false para invitado", authViewModel.puedeComprar())
    }

    @Test
    fun `logout deberia limpiar usuario, marcar no invitado y no permitir comprar`() {
        val usuario = Usuario("Test User", "test@example.com", "dir", "12345678-9", "pass", "cliente")
        authViewModel.usuarioActual.value = usuario
        authViewModel.esInvitado.value = false

        authViewModel.logout()

        assertNull(authViewModel.usuarioActual.value)
        assertFalse(authViewModel.esInvitado.value)
        assertEquals("", authViewModel.mensaje.value)
        assertFalse(authViewModel.puedeComprar())
    }

    @Test
    fun `puedeComprar deberia ser true solo para usuario logueado no invitado`() {
        // sin usuario
        authViewModel.usuarioActual.value = null
        authViewModel.esInvitado.value = false
        assertFalse(authViewModel.puedeComprar())

        // invitado
        authViewModel.loginInvitado()
        assertFalse(authViewModel.puedeComprar())

        // usuario con rol invitado explícito
        val usuarioInvitado = Usuario("Otro", "otro@example.com", "dir", "11111111-1", "pass", "invitado")
        authViewModel.usuarioActual.value = usuarioInvitado
        authViewModel.esInvitado.value = false // aunque el flag esté en false, el rol manda
        assertFalse(authViewModel.puedeComprar())

        // cliente normal
        val usuarioCliente = Usuario("Cliente", "cliente@example.com", "dir", "12345678-9", "pass", "cliente")
        authViewModel.usuarioActual.value = usuarioCliente
        authViewModel.esInvitado.value = false
        assertTrue(authViewModel.puedeComprar())

        //  admin (
        val usuarioAdmin = Usuario("Admin", "admin@example.com", "dir", "98765432-1", "pass", "admin")
        authViewModel.usuarioActual.value = usuarioAdmin
        authViewModel.esInvitado.value = false
        assertTrue(authViewModel.puedeComprar())
    }
}
